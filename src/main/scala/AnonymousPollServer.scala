import cats.effect.*
import cats.effect.implicits.*
import ciris.*
import config.{*, given}
import core.*
import db.*
import domain.*
import monix.newtypes.HasBuilder
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.SelfAwareStructuredLogger

object AnonymousPollServer extends IOApp.Simple:

  def run: IO[Unit] = scaffoldServer.use { (server, serverConfig, logger) =>
    for
      _ <- IO(server) // TODO: Start
      _ <- logger.info(s"Server running on $serverConfig...")
      _ <- IO.never
    yield ()
  }

  private val scaffoldServer =
    for
      logger <- Resource.eval(Slf4jLogger.create[IO])

      given Logger = logger
      given AppConfig    <- appConfigResource
      given DbTransactor <- DbTransactor.buildTransactor()

      _ <- Resource.eval {
        Migrator.migrate() *> logger.info("Migration successful...")
      }

      server <- Resource.eval(IO.unit)
    yield (server, summon[AppConfig].server, logger)
