package entities.dto

import core.json.{*, given}
import entities.*
import entities.dto.*

case class QuestionAnswer(
  questionId: QuestionId,
  answer: Answer
)
