package db

import cats.effect.*
import cats.implicits.*
import config.{AppConfig, DatabaseConfig}
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

  def build(dbConfig: DatabaseConfig): Resource[IO, DbTransactor] =
    for
      fixedThreadPool <- ExecutionContexts.fixedThreadPool[IO](4)
      hikariTransactor <- HikariTransactor.newHikariTransactor[IO](
        driverClassName = "org.h2.Driver",
        url = dbConfig.h2Url,
        user = dbConfig.username,
        pass = dbConfig.password,
        connectEC = fixedThreadPool
      )
    yield new DbTransactor {
      override lazy val transactor: Transactor[IO] = hikariTransactor
    }
