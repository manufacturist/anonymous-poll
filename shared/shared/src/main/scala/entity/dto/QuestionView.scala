package entity.dto

import core.json.{*, given}
import entity.*

case class QuestionView(
  number: QuestionNumber,
  `type`: QuestionType,
  text: Text
)

object QuestionView:
  given Codec[QuestionView] = deriveCodec
