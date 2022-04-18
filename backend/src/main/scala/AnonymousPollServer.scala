package main

import algebra.PollAlgebra
import algebra.impl.PollAlgebraImpl
import cats.effect.*
import cats.effect.implicits.*
import cats.effect.std.Supervisor
import ciris.*
import port.email.{EmailPort, EmailPortFactory}
import port.email.impl.GmailSMTPAdapter
import com.comcast.ip4s.*
import config.{*, given}
import core.*
import db.*
import monix.newtypes.HasBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import org.http4s.server.middleware.{CORS, CORSPolicy}
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.SelfAwareStructuredLogger
import query.{PollSql, QuestionSql, VoterSql}
import routes.{DocRoutes, HealthRoutes, PollRoutes}
import scaffold.Seeder
import sttp.tapir.server.http4s.Http4sServerInterpreter

object AnonymousPollServer extends ResourceApp.Forever:

  def run(args: List[String]): Resource[IO, Unit] =
    for
      logger <- Resource.eval(Slf4jLogger.create[IO])
      config <- appConfigResource

      given Logger    = logger
      given AppConfig = config

      given Supervisor[IO] <- Supervisor[IO]
      given EmailPort      <- EmailPortFactory(config.emailPort)
      given DbTransactor   <- DbTransactor.build(config.db)

      pollAlgebra: PollAlgebra <- Resource.eval(IO.pure(new PollAlgebraImpl(PollSql, VoterSql, QuestionSql)))

      _ <- Migrator(config.db)
      _ <- Seeder(config.env, pollAlgebra)

      httpApp = {
        val pollRoutes: PollRoutes = new PollRoutes(pollAlgebra)

        val apiEndpoints  = pollRoutes.serverEndpoints :+ HealthRoutes.healthEndpoint
        val docsEndpoints = DocRoutes.generateForRedoc(apiEndpoints)
        val httpRoutes    = Http4sServerInterpreter[IO]().toRoutes(apiEndpoints ++ docsEndpoints)

        CORS.policy.withAllowOriginAll.withAllowMethodsAll.withAllowHeadersAll(
          Router("/" -> httpRoutes).orNotFound
        )
      }

      _ <- Resource.eval(logger.info(s"📄 Redoc link at http://127.0.0.1:1337/api/public/redoc"))

      server <- EmberServerBuilder
        .default[IO]
        .withHostOption(Host.fromString(config.server.host))
        .withPort(Port.fromInt(config.server.port).getOrElse(port"1337"))
        .withHttpApp(httpApp)
        .withShutdownTimeout(config.server.shutdownTimeout) // Default is 30[s] & this is bad for ITs
        .build
    yield ()
