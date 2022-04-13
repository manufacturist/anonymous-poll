package entity.dao

import entity.*

case class Voter(
  code: SingleUseVoteCode,
  pollId: PollId,
  emailAddress: EmailAddress
)
