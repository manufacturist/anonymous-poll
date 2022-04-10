package domain.dao

import domain.*

import java.time.OffsetDateTime

case class Poll(
  id: PollId,
  name: Text,
  createdAt: OffsetDateTime
)
