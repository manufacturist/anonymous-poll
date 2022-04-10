package entity.dto

import core.json.{*, given}
import entity.*

sealed trait Answer:
  def questionId: QuestionId

object Answer:
  case class Choice(questionId: QuestionId, answers: List[Text]) extends Answer
  case class Number(questionId: QuestionId, answer: Int)         extends Answer
  case class OpenEnd(questionId: QuestionId, answer: Text)       extends Answer

  given Codec[Answer] = deriveCodec
