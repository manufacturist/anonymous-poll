package entities.dto

import core.json.{*, given}
import entities.PollId
import entities.dto.*

case class AnswerPoll(
  pollId: PollId,
  answers: List[QuestionAnswer]
)
