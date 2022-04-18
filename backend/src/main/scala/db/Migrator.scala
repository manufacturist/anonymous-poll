package db

import cats.effect.*
import core.Logger
import config.*
import org.flywaydb.core.Flyway

object Migrator:

  def apply(dbConfig: DatabaseConfig)(using logger: Logger): Resource[IO, Unit] = Resource.eval(IO.delay {
    val flywayConfig = Flyway
      .configure()
      .dataSource(dbConfig.h2Url, dbConfig.username, dbConfig.password)
      .locations(dbConfig.locations*)
      .validateMigrationNaming(true)

    val flyway = new Flyway(flywayConfig)
    flyway.migrate()
  } *> logger.info("\uD83D\uDEC2 Migration successful..."))
