import sbt.{Def, *}
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport.webpackEmitSourceMaps

lazy val buildFrontend = taskKey[Unit]("build-frontend")

lazy val root = project
  .in(file("."))
  .aggregate(common.jvm, common.js, backend, frontend.js, frontend.jvm, frontendCompile)
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
    Libraries.shared,
    Libraries.jvm,
    Defaults.itSettings,
    IntegrationTest / parallelExecution := false // Sequential suites, parallel tests execution
  )
  .dependsOn(common.jvm)

lazy val frontendCompile = project
  .enablePlugins(WebScalaJSBundlerPlugin)
  .in(file("./frontend-output"))
  .settings(
    scalaJSProjects         := Seq(frontend.js),
    Assets / pipelineStages := Seq(scalaJSPipeline),
    // triggers scalaJSPipeline when using compile or continuous compilation
    Compile / compile := ((Compile / compile) dependsOn scalaJSPipeline).value
  )

lazy val frontend = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .jsConfigure {
    _.enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin, WebScalaJSBundlerPlugin)
      .settings(
        Libraries.js,
        scalaJSUseMainModuleInitializer := true,
        webpackBundlingMode             := BundlingMode.LibraryAndApplication(),
        webpackEmitSourceMaps           := false,

        // Build frontend (JS)
        buildFrontend :=
          // TODO: Rewrite. Increase readability using Def.taskIf / Def.task
          Def
            .ifS(
              Def.task(Option(System.getenv("POLL_IS_FULL_BUILD")).contains(true.toString))
            )(
              Def.task((Compile / fullOptJS / webpack dependsOn Compile / compile).value)
            )(
              Def.ifS(
                Def.task(Option(System.getenv("POLL_IS_WEBPACK_BUILD")).contains(true.toString))
              )(
                Def.task((Compile / fastOptJS / webpack dependsOn Compile / compile).value)
              )(
                Def.task(
                  ((Compile / fastOptJS).map(Seq(_)) dependsOn Compile / compile).value
                )
              )
            )
            .value
      )
      .dependsOn(common.js)
  }
  .jvmConfigure {
    _.enablePlugins(HepekPlugin)
      .settings(
        Libraries.shared,
        Libraries.html,

        // Build frontend (HTML)
        hepekTarget   := target.value / "html",
        buildFrontend := (Compile / hepek).value
      )
      .dependsOn(common.jvm)
  }
  .in(file("./frontend"))
  .settings(
    name := "frontend",
    Settings.common
  )
  .configs(IntegrationTest)

lazy val common = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .jsConfigure {
    _.enablePlugins(ScalaJSBundlerPlugin, ScalaJSWeb)
      .settings(Libraries.js)
  }
  .in(file("./common"))
  .configs(IntegrationTest)
  .settings(
    name := "anonymous-poll-shared",
    Settings.common,
    Libraries.shared,
    Defaults.itSettings
  )
