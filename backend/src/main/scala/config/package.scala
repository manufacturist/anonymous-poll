import cats.effect.*
import ciris.{Secret as _, *}
import entity.*
import monix.newtypes.HasBuilder
import org.http4s.Uri

import concurrent.duration.{DurationInt, FiniteDuration}

package object config:

  given [Base, New](using
    builder: HasBuilder.Aux[New, Base],
    decoder: ConfigDecoder[String, Base]
  ): ConfigDecoder[String, New] =
    decoder.mapEither { (maybeConfigKey, base) =>
      builder.build(base).left.map(buildFailure => ConfigError(buildFailure.toReadableString))
    }

  val localAppConfig: AppConfig = AppConfig(
    env = Environment.LOCAL,
    server = ServerConfig(
      host = "127.0.0.1",
      port = 1337,
      shutdownTimeout = 1.second
    ),
    frontendUris = FrontendUris(
      Uri.unsafeFromString("http://127.0.0.1:8000")
    ),
    db = DatabaseConfig(
      name = "anonymous_poll",
      username = "foo",
      password = "bar",
      locations = "sql" :: Nil
    ),
    purge = PurgeConfig(
      interval = 2.days
    ),
    emailTemplates = EmailTemplatesConfig(
      inviteToPollSubject = SubjectTemplate(Template("Poll %s")),
      inviteToPollContent = ContentTemplate(
        Template(
          """Hi %s 👋
            |
            |You've been invited to participate to the "%s" poll.
            |
            |In order to vote, please proceed to the following link:
            |%s
            | 
            |Cheers 🍻,
            |Anonymous Poll 🎭""".stripMargin
        )
      )
    ),
    emailPort = EmailPortConfig(
      strategy = EmailPortStrategy.NoOp,
      adapterConfig = NoOpEmailConfig
    )
  )

  def loadAppConfig(): Resource[IO, AppConfig] =
    for
      environment <- env(EnvVars.ENVIRONMENT)
        .map(Environment.valueOf)
        .default(localAppConfig.env)
        .resource[IO]

      host <- env(EnvVars.SERVER_HOST).default(localAppConfig.server.host).resource[IO]
      port <- env(EnvVars.SERVER_PORT).as[Int].default(localAppConfig.server.port).resource[IO]

      shutdownTimeout <- env(EnvVars.SERVER_SHUTDOWN_TIMEOUT)
        .as[FiniteDuration]
        .default(localAppConfig.server.shutdownTimeout)
        .resource[IO]

      purgeInterval <- env(EnvVars.PURGE_INTERVAL)
        .as[FiniteDuration]
        .default(localAppConfig.purge.interval)
        .resource[IO]

      baseUri <- env(EnvVars.FRONTEND_BASE_URI)
        .as[String]
        .map(Uri.unsafeFromString)
        .default(localAppConfig.frontendUris.baseUri)
        .resource[IO]

      inviteToPollSubject <- env(EnvVars.INVITE_TO_POLL_SUBJECT)
        .as[SubjectTemplate]
        .default(localAppConfig.emailTemplates.inviteToPollSubject)
        .resource[IO]

      inviteToPollContent <- env(EnvVars.INVITE_TO_POLL_CONTENT)
        .as[ContentTemplate]
        .default(localAppConfig.emailTemplates.inviteToPollContent)
        .resource[IO]

      emailPortStrategy <- env(EnvVars.EMAIL_PORT_STRATEGY)
        .as[EmailPortStrategy]
        .default(localAppConfig.emailPort.strategy)
        .resource[IO]

      emailConfig <- emailPortStrategy match {
        case EmailPortStrategy.Gmail =>
          for
            gmailUsername <- env(EnvVars.GMAIL_USERNAME).as[EmailAddress].resource[IO]
            gmailPassword <- env(EnvVars.GMAIL_PASSWORD).as[Secret].resource[IO]
          yield SMTPConfig(gmailUsername, gmailPassword)

        case EmailPortStrategy.MailChimp =>
          env(EnvVars.MAILCHIMP_API_KEY).as[Secret].resource[IO].map(MailChimpConfig.apply)

        case EmailPortStrategy.NoOp =>
          Resource.eval(IO.pure(NoOpEmailConfig))
      }
    yield AppConfig(
      env = environment,
      server = ServerConfig(
        host = host,
        port = port,
        shutdownTimeout = shutdownTimeout
      ),
      frontendUris = FrontendUris(
        baseUri = baseUri
      ),
      db = localAppConfig.db,
      purge = PurgeConfig(
        interval = purgeInterval
      ),
      emailTemplates = EmailTemplatesConfig(
        inviteToPollSubject = inviteToPollSubject,
        inviteToPollContent = inviteToPollContent
      ),
      emailPort = EmailPortConfig(
        strategy = emailPortStrategy,
        adapterConfig = emailConfig
      )
    )
