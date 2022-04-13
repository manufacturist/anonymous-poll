package query

import entity.*
import entity.dao.*

import java.util.UUID

final class VoterSqlSpec() extends TransactorFixtureSuite:

  private val code = SingleUseVoteCode(UUID.fromString("89b581ee-4a78-43e9-abad-e4c3c1f9f026"))

  test("insert") {
    val voter = Voter(
      code = code,
      pollId = pollId,
      emailAddress = EmailAddress("foo@bar.com")
    )

    check(VoterQueries.insert.toUpdate0(voter))
  }

  test("selectVoterByCodeWhere") {
    check(VoterQueries.selectVoterByCodeWhere(code))
  }
