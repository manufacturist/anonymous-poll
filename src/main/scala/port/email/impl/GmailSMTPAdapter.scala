package port.email.impl

import config.*
import entity.*
import port.email.*
import cats.effect.IO
import cats.effect.std.Supervisor
import cats.effect.kernel.Resource
import javax.mail.*
import javax.mail.internet.*
import java.util.Properties

final class GmailSMTPAdapter(config: SMTPConfig, mailSession: Session)(using Supervisor[IO]) extends EmailPort:

  override def send[A <: Email](email: A): IO[Unit] =
    val send = connectedTransportResource.use { transport =>
      for
        message <- createMimeMessage(email.to, email.subject, email.content)
        _       <- IO.delay(transport.sendMessage(message, message.getAllRecipients))
      yield ()
    }

    summon[Supervisor[IO]].supervise(send).void

  private val connectedTransportResource: Resource[IO, Transport] =
    val transportRetrieve = IO.delay(mailSession.getTransport(SMTPTransport))
    val transportClose    = (t: Transport) => IO.delay(t.close())
    val transportConnect  = (t: Transport) => IO.delay(t.connect(GmailSMTPHost, config.username, config.password))

    for
      transport <- Resource.make(transportRetrieve)(transportClose)
      _         <- Resource.eval(transportConnect(transport))
    yield transport

  private def createMimeMessage(
    to: EmailAddress,
    subject: EmailSubject,
    content: EmailContent
  ): IO[MimeMessage] =
    IO.delay {
      val message: MimeMessage = new MimeMessage(mailSession)
      message.setFrom(new InternetAddress(config.username))
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(to))
      message.setSubject(subject)
      message.setText(content)
      message.saveChanges()
      message
    }

object GmailSMTPAdapter:
  def apply(config: SMTPConfig)(using Supervisor[IO]): Resource[IO, GmailSMTPAdapter] =
    Resource.eval(
      IO.delay {
        val props: Properties = System.getProperties
        props.put("mail.smtp.from", config.username)
        props.put("mail.smtp.user", config.username)
        props.put("mail.smtp.password", config.password)
        props.put("mail.smtp.host", GmailSMTPHost)
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        val session = Session.getInstance(props)

        new GmailSMTPAdapter(config, session)
      }
    )
