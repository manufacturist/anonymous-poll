import cats.effect.IO
import cats.effect.implicits.*
import config.localAppConfig
import endpoint.{HealthEndpoint, Uri}
import entity.*
import entity.dto.*
import entity.dto.AnsweredQuestionView.AnsweredChoiceView
import munit.CatsEffectSuite
import org.http4s.Status
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import sttp.tapir.client.http4s.Http4sClientInterpreter

final class PollApiSpecs extends CatsEffectSuite:

  private val serverFixture = ResourceSuiteLocalFixture("server-run", AnonymousPollServer.run(Nil))
  private val clientFixture = ResourceSuiteLocalFixture("api-client", EmberClientBuilder.default[IO].build)

  override def munitFixtures: Seq[Fixture[?]] = serverFixture :: clientFixture :: Nil

  val pollApi: PollApi = new PollApi(localAppConfig.server.serverUri)

  test("creating a poll and retrieving results will return an empty list") {
    for
      given Client[IO] <- IO(clientFixture())
      pollId           <- pollApi.createPoll(DataFixtures.pollCreate)
      results          <- pollApi.retrieveAnonymousResults(pollId)
    yield assert(results.isEmpty)
  }

  // TODO
  test("answering the poll will return some results") {
    for
      given Client[IO] <- IO(clientFixture())
      pollId           <- pollApi.createPoll(DataFixtures.pollCreate)
      results          <- pollApi.retrieveAnonymousResults(pollId)
    yield assert(results.isEmpty)
  }
