package entities.dao

import entities.*

case class Voter(
  pollId: PollId,
  code: SingleUseVoteCode,
  email: EmailAddress
)
