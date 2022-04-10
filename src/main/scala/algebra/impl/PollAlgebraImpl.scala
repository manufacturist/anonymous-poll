package algebra.impl

import config.*
import entity.*
import algebra.PollAlgebra
import cats.effect.{Clock, IO}
import cats.implicits.*
import cats.effect.std.UUIDGen
import db.DbTransactor
import entity.dao.{Poll, Voter, Question}
import entity.dto.{Answer, PollCreate, Question as QuestionDto}
import query.PollSql

import java.time.*

class PollAlgebraImpl(pollSql: PollSql)(using config: AppConfig, dbTransactor: DbTransactor) extends PollAlgebra:

  override def create(poll: PollCreate): IO[Unit] =
    for
      pollId <- UUIDGen[IO].randomUUID.map(PollId(_))
      now <- Clock[IO].realTime.map(rt => OffsetDateTime.ofInstant(Instant.ofEpochMilli(rt.toMillis), ZoneOffset.UTC))

      newPoll = Poll(
        id = pollId,
        name = poll.name,
        createdAt = now
      )

      newVoterCodes <- poll.recipients.toList.traverse(_ => UUIDGen[IO].randomUUID.map(SingleUseVoteCode(_)))

      pollVoter = newVoterCodes
        .zip(poll.recipients)
        .map((code, email) =>
          Voter(
            code = code,
            pollId = pollId,
            email = email
          )
        )

      newQuestions <- poll.questions.traverse { question =>
        for questionId <- UUIDGen[IO].randomUUID.map(QuestionId(_))
        yield {
          val (questionType, picks, minimum, maximum) = question match {
            case QuestionDto.Choice(text, answers, isMultiPick) => (QuestionType.Choice, Some(answers), None, None)
            case QuestionDto.Number(text, minimum, maximum)     => (QuestionType.Number, None, minimum, maximum)
            case QuestionDto.OpenEnd(text)                      => (QuestionType.OpenEnd, None, None, None)
          }

          Question(
            id = questionId,
            pollId = pollId,
            text = question.text,
            `type` = questionType,
            picks = picks,
            minimum = minimum,
            maximum = maximum
          )
        }
      }

      doobieProgram =
        for
          _ <- pollSql.create(newPoll)
          _ <- pollSql.create(newPoll)
          _ <- pollSql.create(newPoll)
        yield ()

      _ <- dbTransactor.interpret(doobieProgram)
    yield ()

  override def answer(code: SingleUseVoteCode, answers: List[Answer]): IO[Unit] = ???

  override def retrieveAnonymousResults(pollId: PollId): IO[Unit] = ???
