package algebra.impl

import config.*
import entity.*
import algebra.{EmailTemplates, PollAlgebra}
import cats.MonadError
import cats.effect.{Clock, IO}
import cats.implicits.*
import cats.effect.std.{Supervisor, UUIDGen}
import core.Logger
import db.DbTransactor
import doobie.implicits.*
import doobie.free.connection.{ConnectionIO, WeakAsyncConnectionIO as DoobieIO}
import entity.dao.{Answer, Poll, Question, Voter}
import entity.dto.{Answer as AnswerDto, Question as QuestionDto, *}
import port.email.EmailPort
import query.*

import java.time.*

class PollAlgebraImpl(
  pollSql: PollSql,
  voterSql: VoterSql,
  questionSql: QuestionSql
)(using
  config: AppConfig,
  logger: Logger,
  dbTransactor: DbTransactor,
  supervisor: Supervisor[IO],
  emailPort: EmailPort
) extends PollAlgebra:

  // TODO: Move to a syntax package
  extension [F[_], T](boxedF: F[Option[T]])
    def unpack(ifNone: => Throwable)(using F: MonadError[F, Throwable]): F[T] =
      boxedF.flatMap {
        case Some(v) => F.pure(v)
        case None    => F.raiseError(ifNone)
      }

  override def create(poll: PollCreate): IO[PollView] =
    for
      pollId <- UUIDGen[IO].randomUUID.map(PollId(_))
      now <- Clock[IO].realTime.map(rt => OffsetDateTime.ofInstant(Instant.ofEpochMilli(rt.toMillis), ZoneOffset.UTC))
      newVoterCodes <- poll.recipients.toList.traverse(_ => UUIDGen[IO].randomUUID.map(SingleUseVoteCode(_)))

      newPoll = Poll(
        id = pollId,
        name = poll.name,
        createdAt = now
      )

      newPollVoters = newVoterCodes
        .zip(poll.recipients)
        .map { (code, emailAddress) =>
          Voter(
            code = code,
            pollId = pollId,
            emailAddress = emailAddress
          )
        }

      newQuestions = poll.questions.zipWithIndex.map { (question, index) =>
        val (questionType, picks, minimum, maximum) = question match {
          case QuestionDto.Choice(text, answers, isMultiPick) => (QuestionType.Choice, answers, None, None)
          case QuestionDto.Number(text, minimum, maximum)     => (QuestionType.Number, Nil, minimum, maximum)
          case QuestionDto.OpenEnd(text)                      => (QuestionType.OpenEnd, Nil, None, None)
        }

        Question(
          pollId = pollId,
          number = QuestionNumber(index),
          text = question.text,
          `type` = questionType,
          picks = picks,
          minimum = minimum,
          maximum = maximum
        )
      }

      doobieProgram = for
        _ <- pollSql.create(newPoll)
        _ <- voterSql.createVoters(newPollVoters)
        _ <- questionSql.createQuestions(newQuestions)
      yield ()

      _ <- newPollVoters.map { voter =>
        val email = EmailTemplates.InviteToPoll(
          to = voter.emailAddress,
          pollName = newPoll.name,
          voteLink = config.frontendUris.pollRetrievalUri(voter.code)
        )

        val emailSendOp = emailPort
          .send(email)
          .redeem(
            recover = throwable => summon[Logger].error(s"Failed sending email $email; got $throwable"),
            map = identity
          )

        supervisor.supervise(emailSendOp).void
      }.sequence

      _ <- dbTransactor.interpret(doobieProgram)
    yield PollView(
      id = newPoll.id,
      name = newPoll.name,
      questions = newQuestions.map(QuestionView.apply)
    )

  override def findPollByCode(code: SingleUseVoteCode): IO[Option[PollView]] =
    dbTransactor.interpret(pollSql.findPollByCode(code))

  override def answer(code: SingleUseVoteCode, answers: List[AnswerDto]): IO[Unit] =
    val doobieProgram = for
      pollView <- pollSql.findPollByCode(code).unpack(new RuntimeException("Poll not found"))
      voter    <- voterSql.findVoterByCode(code).unpack(new RuntimeException("Voter not found"))

      _ <-
        if pollView.questions.size == answers.size then DoobieIO.unit
        else DoobieIO.raiseError(new RuntimeException("Wrong number of answers"))

      newAnswers = answers.map { answer =>
        val (answers, number) = answer match {
          case AnswerDto.Choice(_, answers) => (answers, None)
          case AnswerDto.Number(_, value)   => (Nil, Some(value))
          case AnswerDto.OpenEnd(_, answer) => (answer :: Nil, None)
        }

        Answer(
          pollId = pollView.id,
          questionNumber = answer.number,
          email = voter.emailAddress,
          answers = answers,
          number = number
        )
      }

      _ <- questionSql.createAnswers(newAnswers)
    yield ()

    dbTransactor.interpret(doobieProgram)

  override def retrieveAnonymousResults(pollId: PollId): IO[List[AnsweredQuestionView]] =
    for answersByQuestions: Map[QuestionView, List[Answer]] <-
        dbTransactor.interpret(questionSql.retrievePollAnswersByQuestions(pollId))
    yield {
      import AnsweredQuestionView.*

      answersByQuestions.toList.map { (questionView, answers) =>
        questionView.`type` match {
          case QuestionType.Choice =>
            val answerStats = answers
              .flatMap(_.answers)
              .groupBy(identity)
              .map { case (key, value) => key.value -> value.size }

            AnsweredChoiceView(answerStats)

          case QuestionType.Number =>
            val numberPicks = answers.flatMap(_.number)
            AnsweredNumberView(numberPicks.sum / numberPicks.size)

          case QuestionType.OpenEnd =>
            val opinions = answers.flatMap(_.answers.headOption)
            AnsweredOpenEndView(opinions)
        }
      }
    }
