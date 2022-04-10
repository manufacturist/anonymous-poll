package query

import db.Composites
import doobie.*
import doobie.implicits.*
import entity.dao.*

object PollSql extends PollSql

trait PollSql:
  def create(poll: Poll): ConnectionIO[Int] =
    PollQueries.insert(poll).run

private[query] object PollQueries extends Composites:
  def insert(poll: Poll): Update0 =
    import poll.*
    sql"INSERT INTO poll ($id, $name, $createdAt)".update
