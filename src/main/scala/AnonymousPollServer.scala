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

  def run: IO[Unit] = bindAll.use { (server, serverConfig, logger) =>
    for
      _ <- IO(server) // TODO: Start
      _ <- logger.info(s"Server running on $serverConfig...")
      _ <- IO.never
    yield ()
  }

  private val bindAll =
    for
      given Logger       <- Resource.eval(Slf4jLogger.create[IO])
      given AppConfig    <- appConfigResource
      given DbTransactor <- DbTransactor.buildTransactor()

      _ <- Resource.eval {
        Migrator() *> summon[Logger].info("Migration successful...")
      }

      server <- Resource.eval(IO.unit)
    yield (server, summon[AppConfig].server, summon[Logger])

  private val appConfigResource: Resource[IO, AppConfig] =
    for
      host                <- env(EnvVars.SERVER_HOST).resource[IO]
      port                <- env(EnvVars.SERVER_PORT).as[Int].resource[IO]
      inviteToPollSubject <- env(EnvVars.INVITE_TO_POLL_SUBJECT).as[SubjectTemplate].resource[IO]
      inviteToPollContent <- env(EnvVars.INVITE_TO_POLL_CONTENT).as[ContentTemplate].resource[IO]
      gmailUsername       <- env(EnvVars.GMAIL_USERNAME).as[EmailAddress].resource[IO]
      gmailPassword       <- env(EnvVars.GMAIL_PASSWORD).as[Password].resource[IO]
    yield AppConfig(
      server = ServerConfig(host, port),
      db = DatabaseConfig("anonymous_poll", "foo", "bar", "sql/v1" :: Nil),
      email = EmailTemplatesConfig(inviteToPollSubject, inviteToPollContent),
      smtp = SMTPConfig(gmailUsername, gmailPassword)
    )
