package db

import cats.effect.*
import cats.implicits.*
import config.AppConfig
import doobie.*
import doobie.implicits.*
import doobie.hikari.*

trait DbTransactor:

  lazy val transactor: Transactor[IO]

  def interpret[A](doobieProgram: ConnectionIO[A]): IO[A] =
    doobieProgram.attemptSql
      .transact(transactor)
      .flatMap {
        case Left(error)   => IO.raiseError[A](error)
        case Right(result) => IO.pure[A](result)
      }

object DbTransactor:

  def buildTransactor()(using config: AppConfig): Resource[IO, DbTransactor] =
    for
      fixedThreadPool <- ExecutionContexts.fixedThreadPool[IO](4)
      hikariTransactor <- HikariTransactor.newHikariTransactor[IO](
        driverClassName = "org.h2.Driver",
        url = config.db.h2Url,
        user = config.db.username,
        pass = config.db.password,
        connectEC = fixedThreadPool
      )
    yield new DbTransactor {
      override lazy val transactor: Transactor[IO] = hikariTransactor
    }
