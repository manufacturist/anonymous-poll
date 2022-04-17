package main.specs

import cats.effect.{IO, Resource}
import cats.effect.implicits.*
import config.localAppConfig
import endpoint.{HealthEndpoint, Uri}
import munit.CatsEffectSuite
import org.http4s.Status
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import tapir.Http4sClientInterpreter

final class SmokeSpecs extends CatsEffectSuite:

  private val serverFixture = ResourceSuiteLocalFixture("server-run", main.AnonymousPollServer.run(Nil))
  private val clientFixture = ResourceSuiteLocalFixture("api-client", EmberClientBuilder.default[IO].build)

  private val clientInterpreter = Http4sClientInterpreter[IO]()

  override def munitFixtures: Seq[Fixture[?]] = serverFixture :: clientFixture :: Nil

  test("server should start") {
    val baseUri            = localAppConfig.server.serverUri
    val (healthRequest, _) = clientInterpreter.toRequest(HealthEndpoint.health, baseUri = Some(baseUri))(())

    for
      client: Client[IO] <- IO(clientFixture())
      responseStatusCode <- client.run(healthRequest).use(response => IO.pure(response.status))
    yield assert(responseStatusCode == Status.Ok)
  }
