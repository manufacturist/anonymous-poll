import sbt.Compile
import webscalajs.WebScalaJS.autoImport.scalaJSProjects

lazy val root = project
  .in(file("."))
  .aggregate(shared.jvm, shared.js, backend, frontend, frontendOutput)
  .settings(
    name    := "anonymous-poll",
    version := "0.0.1"
  )

lazy val backend = project
  .in(file("./backend"))
  .configs(IntegrationTest)
  .settings(
    name := "anonymous-poll-backend",
    Settings.common,
    Dependencies.shared,
    Dependencies.jvm,
    Defaults.itSettings,
    IntegrationTest / parallelExecution := false // Sequential suites, parallel tests execution
  )
  .dependsOn(shared.jvm)

lazy val frontendOutput = project
  .enablePlugins(WebScalaJSBundlerPlugin)
  .in(file("./frontend-output"))
  .settings(
    scalaJSProjects         := Seq(frontend),
    Assets / pipelineStages := Seq(scalaJSPipeline),
    // triggers scalaJSPipeline when using compile or continuous compilation
    Compile / compile := ((Compile / compile) dependsOn scalaJSPipeline).value
  )

lazy val frontend = project
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin, WebScalaJSBundlerPlugin, JSDependenciesPlugin)
  .in(file("./frontend"))
  .configs(IntegrationTest)
  .settings(
    name := "frontend",
    Settings.common,
    Dependencies.shared,
    Dependencies.js,
    scalaJSUseMainModuleInitializer := true
//    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
  )
  .dependsOn(shared.js)

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .jsConfigure(
    _.enablePlugins(ScalaJSBundlerPlugin, ScalaJSWeb)
      .settings(
        Compile / npmDependencies ++= List(
          "buffer" -> "6.0.3",
          "tls"    -> "0.0.1",
          "net"    -> "1.0.2",
          "os"     -> "0.1.2"
        )
      )
  )
  .in(file("./shared"))
  .configs(IntegrationTest)
  .settings(
    name := "anonymous-poll-shared",
    Settings.common,
    Dependencies.shared,
    Defaults.itSettings
//    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
  )
