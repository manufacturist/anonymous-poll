package entity.dto

import core.json.{*, given}
import entity.*

sealed trait AnsweredQuestionView:
  def number: QuestionNumber

object AnsweredQuestionView:

  case class AnsweredChoiceView(
    number: QuestionNumber,
    text: Text,
    // TODO: If you want to use Text here, create a given for map key codec
    results: Map[String, Int]
  ) extends AnsweredQuestionView

  case class AnsweredNumberView(
    number: QuestionNumber,
    text: Text,
    average: Int
  ) extends AnsweredQuestionView

  case class AnsweredOpenEndView(
    number: QuestionNumber,
    text: Text,
    answers: List[Text]
  ) extends AnsweredQuestionView

  given Codec[AnsweredQuestionView] = deriveCodec
