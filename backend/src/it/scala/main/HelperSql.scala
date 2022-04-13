package main

import db.Composites
import doobie.*
import doobie.implicits.*
import entity.{EmailAddress, SingleUseVoteCode}

object HelperSql extends Composites:
  def selectCodeFromVoterWhereEmailAddress(eq: EmailAddress): ConnectionIO[SingleUseVoteCode] =
    sql"SELECT code FROM voter WHERE email_address = $eq".query[SingleUseVoteCode].unique
