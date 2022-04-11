package config

import entity.*
import org.http4s.Uri

final case class AppConfig(
  server: ServerConfig,
  frontendUris: FrontendUris,
  db: DatabaseConfig,
  email: EmailTemplatesConfig,
  smtp: SMTPConfig
)
end AppConfig

case class ServerConfig(host: String, port: Int):
  override def toString: String = s"$host:$port"
end ServerConfig

case class FrontendUris(baseUri: Uri):
  def pollRetrievalUri(code: SingleUseVoteCode): Uri = baseUri / "poll" / code.toString
end FrontendUris

case class EmailTemplatesConfig(inviteToPollSubject: SubjectTemplate, inviteToPollContent: ContentTemplate)
end EmailTemplatesConfig

case class SMTPConfig(username: EmailAddress, password: String)
end SMTPConfig

case class DatabaseConfig(
  name: String,
  username: String,
  password: String,
  locations: List[String]
):
  def h2Url: String = s"jdbc:h2:mem:$name;DB_CLOSE_DELAY=-1"
end DatabaseConfig
