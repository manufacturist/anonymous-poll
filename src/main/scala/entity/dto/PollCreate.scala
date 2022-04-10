package entity.dto

import core.json.{*, given}
import entity.*
import entity.dto.*

case class PollCreate(
  name: Text,
  recipients: Set[EmailAddress],
  questions: List[Question]
)

object PollCreate:
  given Codec[PollCreate] = deriveCodec
