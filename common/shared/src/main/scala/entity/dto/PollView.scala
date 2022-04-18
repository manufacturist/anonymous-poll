package entity.dto

import entity.*
import core.json.{*, given}

case class PollView(
  id: PollId,
  name: PollName,
  questions: List[QuestionView]
)

object PollView:
  given Codec[PollView] = deriveCodec
