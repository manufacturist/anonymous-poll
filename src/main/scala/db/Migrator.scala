package db

import cats.effect.*
import config.*
import org.flywaydb.core.Flyway

object Migrator:

  def migrate()(using appConfig: AppConfig): IO[Unit] = IO.delay {
    val dbConfig = appConfig.db

    val flywayConfig = Flyway
      .configure()
      .dataSource(dbConfig.h2Url, dbConfig.username, dbConfig.password)
      .locations(dbConfig.locations*)
      .validateMigrationNaming(true)

    val flyway = new Flyway(flywayConfig)
    flyway.migrate()
  }
