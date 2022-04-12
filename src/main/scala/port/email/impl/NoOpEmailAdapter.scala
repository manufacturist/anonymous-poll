package port.email.impl

import cats.effect.IO
import core.Logger
import port.email.{Email, EmailPort}

final class NoOpEmailAdapter()(using Logger) extends EmailPort:
  override def send[T <: Email](email: T): IO[Unit] = summon[Logger].info(s"Sending $email")

object NoOpEmailAdapter:
  def apply()(using Logger) = new NoOpEmailAdapter()
