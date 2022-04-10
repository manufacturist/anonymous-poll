package domain.dao

import domain.*

case class Voter(
  pollId: PollId,
  code: SingleUseVoteCode,
  email: EmailAddress
)
