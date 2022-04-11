package entity.dto

import core.json.{*, given}
import entity.*

sealed trait AnsweredQuestionView

object AnsweredQuestionView:
  // TODO: If you want to use Text here, create a given for map key codec
  case class AnsweredChoiceView(results: Map[String, Int]) extends AnsweredQuestionView
  case class AnsweredNumberView(average: Int)              extends AnsweredQuestionView
  case class AnsweredOpenEndView(answers: List[Text])      extends AnsweredQuestionView

  given Codec[AnsweredQuestionView] = deriveCodec
