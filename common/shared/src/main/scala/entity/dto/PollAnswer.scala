package entity.dto

import core.json.{*, given}
import entity.*

case class PollAnswer(
  code: SingleUseVoteCode,
  answers: List[Answer]
)

object PollAnswer:
  given Codec[PollAnswer] = deriveCodec
