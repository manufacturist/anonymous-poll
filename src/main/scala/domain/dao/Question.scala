package domain.dao

import domain.*

case class Question(
  id: QuestionId,
  pollId: PollId,
  text: Text,
  `type`: QuestionType,
  picks: Option[List[Text]],
  minimum: Option[Int],
  maximum: Option[Int]
)
