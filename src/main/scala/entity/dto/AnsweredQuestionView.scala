package entity.dto

import core.json.{*, given}
import entity.*

sealed trait AnsweredQuestionView:
  def number: QuestionNumber

object AnsweredQuestionView:
  // TODO: If you want to use Text here, create a given for map key codec
  case class AnsweredChoiceView(number: QuestionNumber, results: Map[String, Int]) extends AnsweredQuestionView
  case class AnsweredNumberView(number: QuestionNumber, average: Int)              extends AnsweredQuestionView
  case class AnsweredOpenEndView(number: QuestionNumber, answers: List[Text])      extends AnsweredQuestionView

  given Codec[AnsweredQuestionView] = deriveCodec
