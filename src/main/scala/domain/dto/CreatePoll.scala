package domain.dto

import core.json.{*, given}
import domain.*
import domain.dto.*

case class CreatePoll(
  recipients: Set[EmailAddress],
  questions: List[Question]
)

object CreatePoll:
  given Codec[CreatePoll] = deriveCodec
