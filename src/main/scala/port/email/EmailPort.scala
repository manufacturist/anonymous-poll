package port.email

import cats.effect.IO

trait EmailPort: 
  def send[T <: Email](email: T): IO[Unit]
