package entities.dto

import core.json.{*, given}
import entities.*
import entities.dto.*

case class CreatePoll(
  recipients: Set[EmailAddress],
  questions: List[Question]
)

object CreatePoll:
  given Codec[CreatePoll] = deriveCodec
