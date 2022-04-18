package tapir

import cats.effect.Async
import org.http4s.{Request, Response, Uri}
import sttp.tapir.{DecodeResult, Endpoint, PublicEndpoint}

class Http4sClientInterpreter[F[_]: Async] {

  def toRequest[I, E, O, R](
    e: PublicEndpoint[I, E, O, R],
    baseUri: Option[Uri]
  ): I => (Request[F], Response[F] => F[DecodeResult[Either[E, O]]]) =
    new EndpointToHttp4sClient().toHttp4sRequest[Unit, I, E, O, R, F](e, baseUri).apply(())
}
