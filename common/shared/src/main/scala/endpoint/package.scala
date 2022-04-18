import sttp.tapir.Tapir
import sttp.tapir.generic.SchemaDerivation
import sttp.tapir.json.circe.TapirJsonCirce

package object endpoint extends Tapir with TapirJsonCirce with TapirSchemasAndCodecs:

  val version: Int                                        = 1
  val baseEndpoint: Endpoint[Unit, Unit, Unit, Unit, Any] = endpoint.prependIn("api" / s"v$version")

  val Uri: org.http4s.Uri.type               = org.http4s.Uri
  val Request: org.http4s.Request.type       = org.http4s.Request
  val Response: org.http4s.Response.type     = org.http4s.Response
  val HttpRoutes: org.http4s.HttpRoutes.type = org.http4s.HttpRoutes
  val Header: org.http4s.Header.type         = org.http4s.Header
  val Method: org.http4s.Method.type         = org.http4s.Method

  type Request[F[_]]    = org.http4s.Request[F]
  type Response[F[_]]   = org.http4s.Response[F]
  type HttpRoutes[F[_]] = org.http4s.HttpRoutes[F]
  type Uri              = org.http4s.Uri

  val EndpointOutput: sttp.tapir.EndpointOutput.type = sttp.tapir.EndpointOutput
  val StatusCode: sttp.model.StatusCode.type         = sttp.model.StatusCode
  val Schema: sttp.tapir.Schema.type                 = sttp.tapir.Schema
  val SName: sttp.tapir.Schema.SName.type            = sttp.tapir.Schema.SName
  val SchemaType: sttp.tapir.SchemaType.type         = sttp.tapir.SchemaType

  type Schema[T]                = sttp.tapir.Schema[T]
  type Endpoint[S, I, E, O, -R] = sttp.tapir.Endpoint[S, I, E, O, R]
  type ServerEndpoint[-R, F[_]] = sttp.tapir.server.ServerEndpoint[R, F]
