package entity.dto

import core.json.{*, given}
import entity.*

sealed trait Answer:
  def number: QuestionNumber

object Answer:
  case class Choice(number: QuestionNumber, answers: List[Text]) extends Answer
  case class Number(number: QuestionNumber, value: Int)          extends Answer
  case class OpenEnd(number: QuestionNumber, answer: Text)       extends Answer

  given Codec[Answer] = deriveCodec
