package domain.dto

import core.json.{*, given}
import domain.PollId
import domain.dto.*

case class AnswerPoll(
  pollId: PollId,
  answers: List[QuestionAnswer]
)
