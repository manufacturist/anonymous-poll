import sbt.Keys._

object Settings {

  def common = Seq(
    scalaVersion := "3.1.1",
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
}
