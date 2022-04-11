package entity.dto

import core.json.{*, given}
import entity.*
import entity.dto.*

case class PollCreate(
  name: PollName,
  recipients: Set[EmailAddress],
  questions: List[Question]
)

object PollCreate:
  given Codec[PollCreate] = deriveCodec
