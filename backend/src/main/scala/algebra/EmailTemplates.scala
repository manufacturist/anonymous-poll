package algebra

import config.*
import entity.*
import org.http4s.Uri
import port.email.Email

object EmailTemplates:
  case class InviteToPoll(to: EmailAddress, pollName: PollName, voteLink: Uri)(using config: AppConfig) extends Email:

    override def subject: EmailSubject =
      EmailSubject(config.emailTemplates.inviteToPollSubject.render(pollName :: Nil))

    override def content: EmailContent =
      EmailContent(config.emailTemplates.inviteToPollContent.render(to :: pollName :: voteLink :: Nil))

    override def toString: String =
      s"$to - $pollName - ${voteLink.renderString}"
