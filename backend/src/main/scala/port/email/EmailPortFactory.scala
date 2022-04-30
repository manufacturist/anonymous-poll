package port.email

import cats.effect.{IO, Resource}
import cats.effect.std.Supervisor
import config.*
import core.Logger
import port.email.impl.*

object EmailPortFactory:
  def apply(emailPortConfig: EmailPortConfig)(using Logger, Supervisor[IO]): Resource[IO, EmailPort] =
    emailPortConfig.adapterConfig match {
      case smtp: SMTPConfig           => GmailSMTPAdapter(emailPortConfig.adapterConfig.asInstanceOf[SMTPConfig])
      case mailChimp: MailChimpConfig => Resource.eval(IO.pure(MailChimpAdapter()))
      case noOp: NoOpEmailConfig.type => Resource.eval(IO.pure(NoOpEmailAdapter()))
    }
