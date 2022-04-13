package query

import db.Composites
import doobie.*
import doobie.implicits.*
import entity.*
import entity.dao.*

object VoterSql extends VoterSql

trait VoterSql:
  def createVoters(voters: List[Voter]): ConnectionIO[Int] =
    VoterQueries.insert.updateMany(voters)

  def findVoterByCode(code: SingleUseVoteCode): ConnectionIO[Option[Voter]] =
    VoterQueries.selectVoterByCodeWhere(eqCode = code).option

private[query] object VoterQueries extends Composites:
  val insert: Update[Voter] = Update[Voter]("INSERT INTO voter (code, poll_id, email_address) VALUES (?, ?, ?)")

  def selectVoterByCodeWhere(eqCode: SingleUseVoteCode): Query0[Voter] =
    sql"SELECT * FROM voter WHERE code = $eqCode".query
