package entities.dto

import core.json.{*, given}
import entities.*
import entities.dto.*

case class PollCreate(
  recipients: Set[EmailAddress],
  questions: List[Question]
)

object PollCreate:
  given Codec[PollCreate] = deriveCodec
