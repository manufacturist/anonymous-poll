package entity.dao

import entity.*

case class Question(
  pollId: PollId,
  number: QuestionNumber,
  text: Text,
  `type`: QuestionType,
  picks: List[Text],
  minimum: Option[Int],
  maximum: Option[Int]
)
