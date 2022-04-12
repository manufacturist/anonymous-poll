package query

import db.Composites
import doobie.*
import doobie.implicits.*
import entity.*
import entity.dao.*
import entity.dto.{PollView, QuestionView}

object PollSql extends PollSql

trait PollSql:
  def create(poll: Poll): ConnectionIO[Int] =
    PollQueries.insert(poll).run

  def findPollByCode(code: SingleUseVoteCode): ConnectionIO[Option[PollView]] =
    PollQueries
      .selectPollWhereVoterCode(eqCode = code)
      .to[List]
      .map(_.groupMap { (pollId, pollName, questionView) =>
        (pollId, pollName)
      } { (_, _, questionView) =>
        questionView
      }.map { case ((pollId, pollName), groupedQuestionViews) =>
        PollView(
          id = pollId,
          name = pollName,
          questions = groupedQuestionViews
        )
      }.headOption)

  def deletePoll(pollId: PollId): ConnectionIO[Int] =
    PollQueries.deletePollWherePollId(eqPollId = pollId).run

private[query] object PollQueries extends Composites:
  def insert(poll: Poll): Update0 =
    import poll.*
    sql"INSERT INTO poll (id, name, created_at) VALUES ($id, $name, $createdAt)".update

  def selectPollWhereVoterCode(eqCode: SingleUseVoteCode): Query0[(PollId, PollName, QuestionView)] =
    sql"""SELECT p.id, p.name, q.number, q.type, q.text
         |FROM poll AS p
         |JOIN voter AS v ON p.id = v.poll_id
         |JOIN question AS q ON p.id = q.poll_id  
         |WHERE v.code = $eqCode
         |""".stripMargin.query[(PollId, PollName, QuestionView)]

  def deletePollWherePollId(eqPollId: PollId): Update0 =
    sql"DELETE FROM poll WHERE id = $eqPollId".update
