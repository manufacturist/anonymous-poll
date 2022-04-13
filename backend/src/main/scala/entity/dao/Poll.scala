package entity.dao

import entity.*

import java.time.OffsetDateTime

case class Poll(
  id: PollId,
  name: PollName,
  createdAt: OffsetDateTime
)
