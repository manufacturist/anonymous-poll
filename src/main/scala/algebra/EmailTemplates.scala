package algebra

import config.*
import domain.*
import org.http4s.Uri
import port.email.Email

object EmailTemplates:
  case class InviteToPoll(to: EmailAddress, pollName: PollName, voteLink: Uri)(using config: AppConfig) extends Email:

    override def subject: EmailSubject =
      EmailSubject(config.email.inviteToPollSubject.render(pollName :: Nil))

    override def content: EmailContent =
      EmailContent(config.email.inviteToPollContent.render(to :: pollName :: voteLink :: Nil))
