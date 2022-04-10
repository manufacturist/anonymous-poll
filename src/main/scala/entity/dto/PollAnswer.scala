package entity.dto

import core.json.{*, given}
import entity.*
import entity.dto.*

case class PollAnswer(
  pollId: PollId,
  code: SingleUseVoteCode,
  answers: List[QuestionAnswer]
)
