package db

import cats.effect.*
import config.*
import org.flywaydb.core.Flyway

object Migrator:

  def migrate(dbConfig: DatabaseConfig): IO[Unit] = IO.delay {
    val flywayConfig = Flyway
      .configure()
      .dataSource(dbConfig.h2Url, dbConfig.username, dbConfig.password)
      .locations(dbConfig.locations*)
      .validateMigrationNaming(true)

    val flyway = new Flyway(flywayConfig)
    flyway.migrate()
  }
