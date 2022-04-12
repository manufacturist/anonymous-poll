import cats.effect.*
import ciris.*
import entity.*
import monix.newtypes.HasBuilder
import org.http4s.Uri

package object config:

  given [Base, New](using
    builder: HasBuilder.Aux[New, Base],
    decoder: ConfigDecoder[String, Base]
  ): ConfigDecoder[String, New] =
    decoder.mapEither { (maybeConfigKey, base) =>
      builder.build(base).left.map(buildFailure => ConfigError(buildFailure.toReadableString))
    }

  val localAppConfig: AppConfig = AppConfig(
    server = ServerConfig(
      host = "127.0.0.1",
      port = 1337
    ),
    frontendUris = FrontendUris(
      Uri.unsafeFromString("http://127.0.0.1:1337")
    ),
    db = DatabaseConfig(
      name = "anonymous_poll",
      username = "foo",
      password = "bar",
      locations = "sql" :: Nil
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

  val appConfigResource: Resource[IO, AppConfig] =
    for
      host <- env(EnvVars.SERVER_HOST).default(localAppConfig.server.host).resource[IO]
      port <- env(EnvVars.SERVER_PORT).as[Int].default(localAppConfig.server.port).resource[IO]

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
            gmailPassword <- env(EnvVars.GMAIL_PASSWORD).as[Password].resource[IO]
          yield SMTPConfig(gmailUsername, gmailPassword)

        case EmailPortStrategy.NoOp =>
          Resource.eval(IO.pure(NoOpEmailConfig))
      }
    yield AppConfig(
      server = ServerConfig(
        host = host,
        port = port
      ),
      frontendUris = FrontendUris(baseUri),
      db = DatabaseConfig(
        name = "anonymous_poll",
        username = "foo",
        password = "bar",
        locations = "sql" :: Nil
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
