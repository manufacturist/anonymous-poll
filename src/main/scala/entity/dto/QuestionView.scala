package entity.dto

import entity.*

case class QuestionView(
  number: QuestionNumber,
  `type`: QuestionType,
  text: Text
)
