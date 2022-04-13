package routes

import cats.effect.IO
import endpoint.*
import sttp.tapir.{Codec, DecodeResult}
import sttp.tapir.apispec.ExtensionValue
import sttp.tapir.docs.apispec.DocsExtension
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.openapi.Info
import sttp.tapir.redoc.Redoc
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.openapi.circe.yaml.*
import scala.collection.immutable.ListMap

object DocRoutes:

  private val baseApiInfo = Info(
    title = "Anonymous Poll API \uD83C\uDFAD",
    version = "1.0.0"
  )

  def generateForRedoc(
    serverEndpoints: List[ServerEndpoint[Any, IO]]
  ): List[ServerEndpoint[Any, IO]] =
    val apiInfo = baseApiInfo.description("""# Hello Typelevel & Scala 3 ❤️
        |This example was brought to you by [yours truly](https://github.com/manufacturist).""".stripMargin)

    val openApiYaml = OpenAPIDocsInterpreter()
      .toOpenAPI(
        es = serverEndpoints.map(_.endpoint),
        info = apiInfo,
        docsExtensions = Nil
      )
      .toYaml

    Redoc[IO](
      title = apiInfo.title,
      spec = openApiYaml,
      prefix = "api" :: "public" :: "redoc" :: Nil
    )
