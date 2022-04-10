package domain.dao

import domain.*

case class Answer(
  pollId: PollId,
  questionId: QuestionId,
  email: EmailAddress,
  answers: Option[List[String]],
  number: Int
)
