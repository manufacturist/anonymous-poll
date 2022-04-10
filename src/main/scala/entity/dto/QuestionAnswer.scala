package entity.dto

import core.json.{*, given}
import entity.*
import entity.dto.*

case class QuestionAnswer(
  questionId: QuestionId,
  answer: Answer
)
