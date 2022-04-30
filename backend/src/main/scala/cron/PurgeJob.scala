package cron

import cats.effect.{Clock, FiberIO, IO, Resource}
import cats.implicits.*
import config.PurgeConfig
import core.Logger
import db.DbTransactor
import doobie.*
import doobie.free.connection.ConnectionIO
import query.PollSql

import java.time.{Instant, OffsetDateTime, ZoneOffset}

final class PurgeJob(pollSql: PollSql, purgeConfig: PurgeConfig)(using logger: Logger, dbTransactor: DbTransactor):
  def run(): IO[Unit] =
    val doobieProgram = for {
      now <- Clock[ConnectionIO].realTime.map(rt =>
        OffsetDateTime.ofInstant(Instant.ofEpochMilli(rt.toMillis), ZoneOffset.UTC)
      )

      polls <- pollSql.findPollOlderThan(now)
      _     <- polls.map(poll => pollSql.deletePoll(poll.id)).sequence
    } yield polls.size

    dbTransactor.interpret(doobieProgram).map(deletedCount => logger.info(s"Deleted $deletedCount polls"))
      *> IO.sleep(purgeConfig.interval) *> run()

object PurgeJob:
  def apply(pollSql: PollSql, purgeConfig: PurgeConfig)(using Logger, DbTransactor): Resource[IO, FiberIO[Unit]] =
    Resource.make(new PurgeJob(pollSql, purgeConfig).run().start)(_.cancel)
