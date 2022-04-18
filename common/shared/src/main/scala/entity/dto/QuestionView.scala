package entity.dto

import core.json.{*, given}
import entity.*

case class QuestionView(
  number: QuestionNumber,
  `type`: QuestionType,
  text: Text,
  picks: List[Text],
  min: Option[Int],
  max: Option[Int]
)

object QuestionView:
  given Codec[QuestionView] = deriveCodec
