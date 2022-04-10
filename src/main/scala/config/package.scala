import cats.effect.{IO, Resource}
import ciris.*
import entities.{ContentTemplate, EmailAddress, Password, SubjectTemplate}
import monix.newtypes.HasBuilder

package object config:

  given [Base, New](using
    builder: HasBuilder.Aux[New, Base],
    decoder: ConfigDecoder[String, Base]
  ): ConfigDecoder[String, New] =
    decoder.mapEither { (maybeConfigKey, base) =>
      builder.build(base).left.map(buildFailure => ConfigError(buildFailure.toReadableString))
    }

  val appConfigResource: Resource[IO, AppConfig] =
    for
      host                <- env(EnvVars.SERVER_HOST).resource[IO]
      port                <- env(EnvVars.SERVER_PORT).as[Int].resource[IO]
      inviteToPollSubject <- env(EnvVars.INVITE_TO_POLL_SUBJECT).as[SubjectTemplate].resource[IO]
      inviteToPollContent <- env(EnvVars.INVITE_TO_POLL_CONTENT).as[ContentTemplate].resource[IO]
      gmailUsername       <- env(EnvVars.GMAIL_USERNAME).as[EmailAddress].resource[IO]
      gmailPassword       <- env(EnvVars.GMAIL_PASSWORD).as[Password].resource[IO]
    yield AppConfig(
      server = ServerConfig(
        host = host,
        port = port
      ),
      db = DatabaseConfig(
        name = "anonymous_poll",
        username = "foo",
        password = "bar",
        locations = "sql" :: Nil
      ),
      email = EmailTemplatesConfig(
        inviteToPollSubject = inviteToPollSubject,
        inviteToPollContent = inviteToPollContent
      ),
      smtp = SMTPConfig(
        username = gmailUsername,
        password = gmailPassword
      )
    )
