package entity.dto

import core.json.{*, given}
import entity.*
import entity.dto.*

case class PollAnswer(
  code: SingleUseVoteCode,
  answers: List[Answer]
)

object PollAnswer:
  given Codec[PollAnswer] = deriveCodec
