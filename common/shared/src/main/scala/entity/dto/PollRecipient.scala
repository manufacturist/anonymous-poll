package entity.dto

import core.json.{*, given}
import entity.*

case class PollRecipient(
  emailAddress: EmailAddress,
  voteWeight: Option[VoteWeight]
)

object PollRecipient:
  given Codec[PollRecipient] = deriveCodec
