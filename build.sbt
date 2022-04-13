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
  .enablePlugins(ScalaJSPlugin, JSDependenciesPlugin)
  .in(file("./frontend"))
  .configs(IntegrationTest)
  .settings(
    name := "anonymous-poll-frontend",
    Settings.common,
    Dependencies.shared,
    Dependencies.js
  )
  .dependsOn(shared.js)

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .in(file("./shared"))
  .configs(IntegrationTest)
  .settings(
    name := "anonymous-poll-shared",
    Settings.common,
    Dependencies.shared,
    Defaults.itSettings
  )
