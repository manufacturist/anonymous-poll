package domain

import core.json.{*, given}

case class CreatePoll(
  recipients: List[EmailAddress],
  questions: List[Question]
)

object CreatePoll:
  given Codec[CreatePoll] = deriveCodec
