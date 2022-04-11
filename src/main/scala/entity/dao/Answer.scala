package entity.dao

import entity.*

case class Answer(
  pollId: PollId,
  questionNumber: QuestionNumber,
  email: EmailAddress,
  answers: List[Text],
  number: Option[Int]
)
