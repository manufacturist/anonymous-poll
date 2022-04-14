import sbt.Compile

lazy val root = project
  .in(file("."))
  .aggregate(shared.jvm, shared.js, backend, frontend)
  .settings(
    name    := "anonymous-poll",
    version := "0.1.0-SNAPSHOT"
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
    IntegrationTest / parallelExecution := false // Sequential suites, parallel suite-tests execution
  )
  .dependsOn(shared.jvm)

lazy val frontend = project
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin, WebScalaJSBundlerPlugin, JSDependenciesPlugin)
  .in(file("./frontend"))
  .configs(IntegrationTest)
  .settings(
    name := "anonymous-poll-frontend",
    Settings.common,
    Dependencies.shared,
    Dependencies.js,
    webpackBundlingMode             := BundlingMode.LibraryAndApplication(),
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
    Compile / npmDependencies ++= List(
      "buffer" -> "6.0.3",
      "tls"    -> "0.0.1",
      "net"    -> "1.0.2",
      "os"     -> "0.1.2"
    )
  )
  .dependsOn(shared.js)

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .jsConfigure(project => project.enablePlugins(ScalaJSBundlerPlugin))
  .in(file("./shared"))
  .configs(IntegrationTest)
  .settings(
    name := "anonymous-poll-shared",
    Settings.common,
    Dependencies.shared,
    Defaults.itSettings,
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) }
  )
