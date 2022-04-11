package entity.dto

import core.json.{*, given}
import entity.*
import entity.dao.Question as QuestionDao

case class QuestionView(
  number: QuestionNumber,
  `type`: QuestionType,
  text: Text
)

object QuestionView:
  def apply(question: QuestionDao): QuestionView =
    QuestionView(
      number = question.number,
      `type` = question.`type`,
      text = question.text
    )

  given Codec[QuestionView] = deriveCodec
