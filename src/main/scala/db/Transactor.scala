package db

import cats.effect.*
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import doobie.hikari.*

trait DbTransactor:

  lazy val transactor: Transactor[IO]

  def interpret[A](doobieProgram: ConnectionIO[A]): IO[A] =
    doobieProgram.attemptSql
      .transact(transactor)
      .flatMap {
        case Left(anomaly) => IO.raiseError[A](anomaly)
        case Right(x)      => IO.pure[A](x)
      }

object DbTransactor:

  def buildTransactor(): Resource[IO, DbTransactor] =
    for
      fixedThreadPool <- ExecutionContexts.fixedThreadPool[IO](4)
      hikariTransactor <- HikariTransactor.newHikariTransactor[IO](
        driverClassName = "org.h2.Driver",
        url = "jdbc:h2:mem:anonymous_poll;DB_CLOSE_DELAY=-1",
        user = "foo",
        pass = "foo",
        connectEC = fixedThreadPool
      )
    yield new DbTransactor {
      override lazy val transactor: HikariTransactor[IO] = hikariTransactor
    }
