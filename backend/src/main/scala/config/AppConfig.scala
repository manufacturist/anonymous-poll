package config

import ciris.ConfigDecoder
import entity.*
import org.http4s.Uri

import scala.concurrent.duration.FiniteDuration

final case class AppConfig(
  env: Environment,
  server: ServerConfig,
  frontendUris: FrontendUris,
  db: DatabaseConfig,
  emailTemplates: EmailTemplatesConfig,
  emailPort: EmailPortConfig
)
end AppConfig

enum Environment:
  case LOCAL, DEV, STG, PROD

case class ServerConfig(host: String, port: Int, shutdownTimeout: FiniteDuration):
  override def toString: String = s"$host:$port"

  def serverUri: Uri = Uri.unsafeFromString(s"http://${this.toString}")
end ServerConfig

case class FrontendUris(baseUri: Uri):
  def pollRetrievalUri(code: SingleUseVoteCode): Uri = baseUri / "#Answer" +? ("code", code.toString)
end FrontendUris

case class EmailTemplatesConfig(inviteToPollSubject: SubjectTemplate, inviteToPollContent: ContentTemplate)
end EmailTemplatesConfig

enum EmailPortStrategy:
  case Gmail, NoOp

object EmailPortStrategy:
  given ConfigDecoder[String, EmailPortStrategy] =
    ConfigDecoder.instance[String, EmailPortStrategy] { (_, value) => Right(EmailPortStrategy.valueOf(value)) }

sealed trait EmailConfig

case class SMTPConfig(username: EmailAddress, password: String) extends EmailConfig
end SMTPConfig

case object NoOpEmailConfig extends EmailConfig
end NoOpEmailConfig

case class EmailPortConfig(
  strategy: EmailPortStrategy,
  adapterConfig: EmailConfig
)
end EmailPortConfig

case class DatabaseConfig(
  name: String,
  username: String,
  password: String,
  locations: List[String]
):
  def h2Url: String = s"jdbc:h2:mem:$name;DB_CLOSE_DELAY=-1"
end DatabaseConfig
