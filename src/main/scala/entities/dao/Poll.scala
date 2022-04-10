package entities.dao

import entities.*

import java.time.OffsetDateTime

case class Poll(
  id: PollId,
  name: Text,
  createdAt: OffsetDateTime
)
