package domain.dto

import core.json.{*, given}
import domain.*
import domain.dto.*

case class QuestionAnswer(
  questionId: QuestionId,
  answer: Answer
)
