val scala3V          = "3.1.1"
val catsV            = "3.3.11"
val circeV           = "0.14.1"
val cirisV           = "2.3.2"
val doobieV          = "1.0.0-RC2"
val flywayV          = "8.5.7"
val h2V              = "2.1.210"
val http4sV          = "0.23.11"
val log4catsV        = "2.2.0"
val logbackV         = "1.2.11"
val munitCatsEffectV = "1.0.7"
val newtypesV        = "0.2.1"
val tapirV           = "1.0.0-M6"

lazy val root = project
  .in(file("."))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    name                                := "anonymous-poll",
    version                             := "0.1.0-SNAPSHOT",
    scalaVersion                        := scala3V,
    IntegrationTest / parallelExecution := false, // Sequential suites, parallel suite-tests execution
    libraryDependencies ++= Seq(
      // Configuration
      "is.cir" %% "ciris" % cirisV withSources (),

      // Newtypes
      "io.monix" %% "newtypes-core" % newtypesV withSources (),

      // JSON
      "io.circe" %% "circe-core"    % circeV withSources (),
      "io.circe" %% "circe-generic" % circeV withSources (),

      // H2 - In-memory database - Override doobie transitive one, since it brings 3 CVEs (not impacting us)
      "com.h2database" % "h2" % h2V withSources (),

      // Database communication
      "org.tpolecat" %% "doobie-core"   % doobieV withSources (),
      "org.tpolecat" %% "doobie-hikari" % doobieV withSources (),
      "org.tpolecat" %% "doobie-h2"     % doobieV withSources (),
      "org.tpolecat" %% "doobie-munit"  % doobieV withSources (),

      // Database migration
      "org.flywaydb" % "flyway-core" % flywayV withSources (),

      // IO monad
      "org.typelevel" %% "cats-effect" % catsV withSources (),

      // Http4s dependencies
      "org.http4s" %% "http4s-ember-server" % http4sV withSources (),
      "org.http4s" %% "http4s-ember-client" % http4sV withSources (),
      "org.http4s" %% "http4s-circe"        % http4sV withSources (),
      "org.http4s" %% "http4s-dsl"          % http4sV withSources (),

      // Tapir - Endpoint descriptions conversions to Server Http Endpoints, Http Clients & Documentation
      "com.softwaremill.sttp.tapir" %% "tapir-core"               % tapirV withSources (),
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe"         % tapirV withSources (),
      "com.softwaremill.sttp.tapir" %% "tapir-cats"               % tapirV withSources (),
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"      % tapirV withSources (),
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-client"      % tapirV withSources (),
      "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"       % tapirV withSources (),
      "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % tapirV withSources (),
      "com.softwaremill.sttp.tapir" %% "tapir-redoc"              % tapirV withSources (),

      // SMTP email sending
      "com.sun.mail" % "javax.mail" % "1.6.2" withSources (),

      // Testing
      "org.typelevel" %% "munit-cats-effect-3" % munitCatsEffectV % "test, it",

      // Logging
      "org.typelevel" %% "log4cats-slf4j"  % log4catsV withSources (),
      "ch.qos.logback" % "logback-classic" % logbackV % Runtime withSources ()
    ),
    scalacOptions ++= Seq(
      "-encoding",
      "utf8",
      "-Xfatal-warnings",
      "-deprecation",
      "-unchecked",
      "-language:implicitConversions",
      "-language:higherKinds",
      "-language:existentials",
      "-language:postfixOps",
      "-source:future",
      "-new-syntax"
    )
  )
