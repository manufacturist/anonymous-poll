package entity.dto

import entity.*

case class PollView(
  id: PollId,
  name: PollName,
  questions: List[QuestionView]
)
