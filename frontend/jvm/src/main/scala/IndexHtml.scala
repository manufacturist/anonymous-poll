import ba.sake.hepek.core.Renderable
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import ciris.*
import config.*
import entity.*
import scalatags.Text.all.*

import java.nio.file.*
import scala.concurrent.*
import scala.concurrent.duration.*

object IndexHtml extends Renderable with CommonEnvVars {

  override def render(): String =
    val htmlRender = for
      environment <- env(ENVIRONMENT).map(Environment.valueOf).default(Environment.DEV).resource[IO]
      language    <- env(CLIENT_LANGUAGE).map(DocumentLanguage.valueOf).default(DocumentLanguage.en).resource[IO]
    yield renderFromConfig(environment, language)

    htmlRender.use(IO.pure).unsafeRunSync()

  override def relPath: Path =
    Paths.get("index.html")

  private def renderFromConfig(environment: Environment, language: DocumentLanguage): String =
    val scripts = environment match {
      case Environment.PROD =>
        // TODO: Figure out how to create production ready tailwind css
        script(src := "https://cdn.tailwindcss.com") :: script(
          `type` := "module",
          src    := "./js/frontend-opt-bundle.js"
        ) :: Nil
      case _ =>
        script(src := "https://cdn.tailwindcss.com") :: script(
          `type` := "text/javascript",
          src    := "./js/frontend-fastopt-library.js"
        ) :: script(
          `type` := "text/javascript",
          src    := "./js/frontend-fastopt-loader.js"
        ) :: script(
          defer,
          `type` := "text/javascript",
          src    := "./js/frontend-fastopt.js"
        ) :: Nil
    }

    "<!DOCTYPE html>" + html(lang := language.toString)(
      head(
        meta(charset := "UTF-8"),
        meta(name    := "viewport", content := "width=device-width, initial-scale=1.0"),
        tag("title")("Anoynmous Poll \uD83C\uDFAD"),
        scripts
      ),
      body(id := "anonymous-poll-app")
    )
}
