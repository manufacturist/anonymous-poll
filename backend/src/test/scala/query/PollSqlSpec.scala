package query

import cats.effect.{IO, Resource}
import config.DatabaseConfig
import db.Migrator
import doobie.util.transactor.Transactor
import entity.{SingleUseVoteCode, *}
import entity.dao.*
import munit.CatsEffectSuite

import java.time.OffsetDateTime
import java.util.UUID

final class PollSqlSpec() extends TransactorFixtureSuite:

  test("insert") {
    val poll = Poll(
      id = pollId,
      name = PollName("test"),
      createdAt = OffsetDateTime.MIN
    )

    check(PollQueries.insert(poll))
  }

  test("selectPollWhereVoterCode") {
    val code = SingleUseVoteCode(UUID.fromString("cd2c7abf-bc44-4f3e-95b5-66ebbddebf38"))
    check(PollQueries.selectPollWhereVoterCode(code))
  }

  test("selectPollWhereCreatedLowerThan") {
    val date = OffsetDateTime.MAX
    check(PollQueries.selectPollWhereCreatedLowerThan(date))
  }

  test("deletePollWherePollId") {
    check(PollQueries.deletePollWherePollId(pollId))
  }
