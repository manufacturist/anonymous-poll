package query

import cats.effect.{IO, Resource}
import config.DatabaseConfig
import db.Migrator
import doobie.util.transactor.Transactor
import entity.PollId
import munit.CatsEffectSuite

import java.util.UUID

trait TransactorFixtureSuite extends CatsEffectSuite with doobie.munit.IOChecker:

  private val database = "poll"
  private val user     = "foo"
  private val pass     = "bar"

  val pollId = PollId(UUID.fromString("cd2c7abf-bc44-4f3e-95b5-66ebbddebf38"))

  val transactor: doobie.Transactor[IO] = Transactor.fromDriverManager[IO](
    driver = "org.h2.Driver",
    url = s"jdbc:h2:mem:$database",
    user = user,
    pass = pass
  )

  private val migrationFixture = ResourceSuiteLocalFixture(
    "migration-fixture",
    Resource.eval(Migrator.migrate(DatabaseConfig(database, user, pass, "sql" :: Nil)))
  )

  override def munitFixtures = List(migrationFixture)
