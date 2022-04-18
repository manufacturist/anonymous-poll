package main.specs

import cats.effect.IO
import cats.effect.implicits.*
import client.PollApiClient
import config.localAppConfig
import core.Logger
import db.{DbTransactor, Migrator}
import entity.*
import entity.dto.{QuestionView, *}
import entity.dto.AnsweredQuestionView.AnsweredChoiceView
import main.*
import main.Fixtures.*
import munit.CatsEffectSuite
import org.http4s.Status
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import org.typelevel.log4cats.slf4j.Slf4jLogger

final class PollApiSpecs extends CatsEffectSuite:

  private val pollApiClient =
    for given Client[IO] <- EmberClientBuilder.default[IO].build
    yield new PollApiClient(localAppConfig.server.serverUri)

  private val serverFixture        = ResourceSuiteLocalFixture("server-run", main.AnonymousPollServer.run(Nil))
  private val pollApiClientFixture = ResourceSuiteLocalFixture("api-client", pollApiClient)
  private val transactorFixture    = ResourceSuiteLocalFixture("transactor", DbTransactor.build(localAppConfig.db))

  given Logger = Slf4jLogger.getLogger[IO]

  override def munitFixtures: Seq[Fixture[?]] = serverFixture :: pollApiClientFixture :: transactorFixture :: Nil

  override def afterEach(context: AfterEach): Unit = {
    import doobie.implicits.*

    val afterEachCleanup =
      for
        transactor <- IO(transactorFixture())
        _          <- transactor.interpret(sql"DELETE FROM poll".update.run) // TODO: Investigate h2 TRUNCATE TABLE
        _          <- Migrator(localAppConfig.db).use_
      yield super.afterEach(context)

    afterEachCleanup.unsafeRunSync()
  }

  test("creating a poll and retrieving results will return an empty list") {
    for
      pollApi <- IO(pollApiClientFixture())
      pollId  <- pollApi.createPoll(pollCreate)
      results <- pollApi.retrieveAnonymousResults(pollId)
    yield assert(results.isEmpty)
  }

  test("answering the poll will return some results") {
    for
      pollApi                  <- IO(pollApiClientFixture())
      transactor: DbTransactor <- IO(transactorFixture())

      pollId <- pollApi.createPoll(pollCreate)
      code   <- transactor.interpret(HelperSql.selectCodeFromVoterWhereEmailAddress(eq = fooEmailAddress))

      q1 = QuestionNumber(1)
      q2 = QuestionNumber(2)
      q3 = QuestionNumber(3)

      pollView <- pollApi.findPollByCode(code).flatTap {
        case Some(pollView) =>
          IO {
            import QuestionType.*

            assert(pollView.name == pollCreate.name)
            assert(pollView.questions.size == 3)

            val expectedQuestionViews = List(
              QuestionView(q1, Choice, pollCreate.questions.head.text, choiceQuestion.answers, None, None),
              QuestionView(q2, Number, pollCreate.questions(1).text, Nil, Some(0), None),
              QuestionView(q3, OpenEnd, pollCreate.questions(2).text, Nil, None, None)
            )

            assert(pollView.questions.intersect(expectedQuestionViews).size == 3)
          }
        case None => IO(munit.Assertions.fail(s"Couldn't find poll view for code $code"))
      }

      q3Answer = Text(
        "The learning curve has improved since CE2 & the community " +
          "is quite helpful on #Discord - https://sca.la/typeleveldiscord"
      )

      pollAnswer = PollAnswer(
        code = code,
        answers = List(
          Answer.Choice(q1, choiceQuestion.text :: Nil),
          Answer.Number(q2, 1),
          Answer.OpenEnd(q3, q3Answer)
        )
      )

      _ <- pollApi.answerPoll(pollAnswer)

      results <- pollApi.retrieveAnonymousResults(pollId)
    yield {
      assert(results.size == 3)

      import AnsweredQuestionView.*

      val expectedChoiceResult = AnsweredChoiceView(
        q1,
        pollCreate.questions.head.text,
        Map(choiceQuestion.text -> 1)
      )

      val expectedNumberResult  = AnsweredNumberView(q2, pollCreate.questions(1).text, 1)
      val expectedOpenEndResult = AnsweredOpenEndView(q3, pollCreate.questions(2).text, q3Answer :: Nil)

      assert(results.contains(expectedChoiceResult))
      assert(results.contains(expectedNumberResult))
      assert(results.contains(expectedOpenEndResult))
    }
  }

  test("answering the same poll twice yields error") {
    for
      pollApi                  <- IO(pollApiClientFixture())
      transactor: DbTransactor <- IO(transactorFixture())

      pollId <- pollApi.createPoll(pollCreate.copy(questions = pollCreate.questions.head :: Nil))
      code   <- transactor.interpret(HelperSql.selectCodeFromVoterWhereEmailAddress(eq = fooEmailAddress))

      pollAnswer = PollAnswer(
        code = code,
        answers = Answer.Choice(QuestionNumber(1), choiceQuestion.text :: Nil) :: Nil
      )

      _      <- pollApi.answerPoll(pollAnswer)
      result <- pollApi.answerPoll(pollAnswer).attempt

    // TODO: If error types are implemented, do yourself a favour and assert against an exact error type
    yield assert(result.isLeft)
  }
