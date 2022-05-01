package cron

import cats.effect.{Clock, FiberIO, IO, Resource}
import cats.implicits.*
import config.PurgeConfig
import core.Logger
import db.DbTransactor
import doobie.*
import doobie.free.connection.ConnectionIO
import fs2.Stream
import query.PollSql

import java.time.{Instant, OffsetDateTime, ZoneOffset}

final class PurgeJob(pollSql: PollSql, purgeConfig: PurgeConfig)(using logger: Logger, dbTransactor: DbTransactor):
  def run(): IO[Unit] =
    Stream
      .awakeEvery[IO](purgeConfig.interval)
      .evalMap(_ => deleteOldPolls())
      .compile
      .drain

  private def deleteOldPolls(): IO[Unit] =
    val doobieProgram = for {
      now <- Clock[ConnectionIO].realTime.map(rt =>
        OffsetDateTime.ofInstant(Instant.ofEpochMilli(rt.toMillis), ZoneOffset.UTC)
      )

      deletedCount <- pollSql.deletePollsOlderThan(now)
    } yield deletedCount

    for
      deletedCount <- dbTransactor.interpret(doobieProgram)
      _            <- logger.info(s"Deleted $deletedCount polls")
    yield ()

object PurgeJob:
  def apply(pollSql: PollSql, purgeConfig: PurgeConfig)(using Logger, DbTransactor): Resource[IO, FiberIO[Unit]] =
    Resource.make(new PurgeJob(pollSql, purgeConfig).run().start)(_.cancel)
