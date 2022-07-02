package entity.dto

import core.json.{*, given}
import entity.*

case class PollCreate(
  name: PollName,
  recipients: List[PollRecipient],
  questions: List[Question]
)

object PollCreate:
  given Codec[PollCreate] = deriveCodec
