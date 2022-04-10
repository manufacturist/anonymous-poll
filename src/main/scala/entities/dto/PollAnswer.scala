package entities.dto

import core.json.{*, given}
import entities.*
import entities.dto.*

case class PollAnswer(
  pollId: PollId,
  code: SingleUseVoteCode,
  answers: List[QuestionAnswer]
)
