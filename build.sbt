import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport.webpackEmitSourceMaps

lazy val root = project
  .in(file("."))
  .aggregate(common.jvm, common.js, backend, frontend)
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
  .dependsOn(common.jvm)

lazy val frontend = project
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin, WebScalaJSBundlerPlugin, JSDependenciesPlugin)
  .in(file("./frontend"))
  .configs(IntegrationTest)
  .settings(
    name := "frontend",
    Settings.common,
    Dependencies.shared,
    Dependencies.js,
    scalaJSUseMainModuleInitializer := true,
    webpackBundlingMode             := BundlingMode.LibraryAndApplication(),
    webpackEmitSourceMaps           := false
  )
  .dependsOn(common.js)

lazy val common = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .jsConfigure(
    _.enablePlugins(ScalaJSBundlerPlugin, ScalaJSWeb)
      .settings(
          // Was required when fs2 was used on `frontend`
//        Compile / npmDependencies ++= List(
//          "tls" -> "0.0.1",
//          "net" -> "1.0.2",
//          "os"  -> "0.1.2"
//        ),
        Dependencies.js
      )
  )
  .in(file("./common"))
  .configs(IntegrationTest)
  .settings(
    name := "anonymous-poll-shared",
    Settings.common,
    Dependencies.shared,
    Defaults.itSettings
  )
