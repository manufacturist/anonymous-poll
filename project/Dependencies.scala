import sbt._
import sbt.Keys._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object Dependencies {
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
  val scalaCssV        = "1.0.0"
  val scalaJsDomV      = "2.1.0"
  val scalaTagsV       = "0.11.1"
  val sttpClient3V     = "3.5.1"
  val tapirV           = "1.0.0-M6"

  def shared = libraryDependencies ++= Seq(
    // Configuration
    "is.cir" %% "ciris" % cirisV withSources (),

    // Newtypes
    "io.monix" %%% "newtypes-core" % newtypesV withSources (),

    // JSON
    "io.circe" %%% "circe-core"    % circeV withSources (),
    "io.circe" %%% "circe-generic" % circeV withSources (),

    // IO monad
    "org.typelevel" %%% "cats-effect" % catsV withSources (),

    // Http4s dependencies
    "org.http4s" %%% "http4s-circe"        % http4sV withSources (),
    "org.http4s" %%% "http4s-dsl"          % http4sV withSources (),
    "org.http4s" %%% "http4s-ember-client" % http4sV withSources (),

    // Tapir - Endpoint descriptions conversions to Server Http Endpoints, Http Clients & Documentation
    "com.softwaremill.sttp.tapir" %%% "tapir-core"        % tapirV withSources (),
    "com.softwaremill.sttp.tapir" %%% "tapir-json-circe"  % tapirV withSources (),
    "com.softwaremill.sttp.tapir" %%% "tapir-sttp-client" % tapirV withSources (),

    // Sttp client backend
    "com.softwaremill.sttp.client3" %%% "core"           % sttpClient3V withSources (),
    "com.softwaremill.sttp.client3" %%% "cats"           % sttpClient3V withSources (), // frontend-side
    "com.softwaremill.sttp.client3"  %% "http4s-backend" % sttpClient3V withSources (), // backend-side

    // Testing
    "org.typelevel" %%% "munit-cats-effect-3" % munitCatsEffectV % "test, it",

    // Logging
    "org.typelevel" %% "log4cats-slf4j"  % log4catsV withSources (),
    "ch.qos.logback" % "logback-classic" % logbackV % Runtime withSources ()
  )

  def jvm = libraryDependencies ++= Seq(
    // Http4s server dependencies
    "org.http4s" %% "http4s-ember-server" % http4sV withSources (),

    // Tapir server dependencies
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"      % tapirV withSources (),
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"       % tapirV withSources (),
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % tapirV withSources (),
    "com.softwaremill.sttp.tapir" %% "tapir-redoc"              % tapirV withSources (),

    // H2 - In-memory database - Override doobie transitive one, since it brings 3 CVEs (not impacting us)
    "com.h2database" % "h2" % h2V withSources (),

    // Database communication
    "org.tpolecat" %% "doobie-core"   % doobieV withSources (),
    "org.tpolecat" %% "doobie-hikari" % doobieV withSources (),
    "org.tpolecat" %% "doobie-h2"     % doobieV withSources (),
    "org.tpolecat" %% "doobie-munit"  % doobieV withSources (),

    // Database migration
    "org.flywaydb" % "flyway-core" % flywayV withSources (),

    // SMTP email sending
    "com.sun.mail" % "javax.mail" % "1.6.2" withSources ()
  )

  def js = libraryDependencies ++= Seq(
    // Frontend dependencies
    "org.scala-js"                 %%% "scalajs-dom" % scalaJsDomV withSources (),
    "com.lihaoyi"                  %%% "scalatags"   % scalaTagsV withSources (),
    "com.github.japgolly.scalacss" %%% "core"        % scalaCssV withSources ()
  )
}
