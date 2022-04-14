package client

import cats.effect.IO
import endpoint.{PollEndpoints, Response}
import entity.dto.{AnsweredQuestionView, PollAnswer, PollCreate, PollView}
import entity.{PollId, SingleUseVoteCode}
import org.http4s.client.Client
import org.http4s.{Request, Response, Uri}
import sttp.model.Uri as SttpUri
import sttp.tapir.client.sttp.{SttpClientInterpreter, WebSocketToPipe}

import java.net.URI as JavaUri
import scala.concurrent.Future

final class PollApiClient(baseUri: Uri):

  private val baseSttpUri = SttpUri(JavaUri.create(baseUri.renderString))

//  TODO: tapir-http4s-client lacks Scala.js support :( therefore this snippet is rendered useless
//  extension [E, O](httpIO: (Request[IO], Response[IO] => IO[DecodeResult[Either[E, O]]]))
//    def run()(using client: Client[IO]) =
//      val (request, parseResponse) = httpIO
//
//      client.run(request).use {
//        parseResponse(_).flatMap {
//          case Value(Right(value))   => IO.pure(value)
//          case Value(Left(unwanted)) => IO.raiseError(new RuntimeException(s"Got $unwanted"))
//          case fail: Failure         => IO.raiseError(new RuntimeException(s"Couldn't decode due to $fail"))
//        }
//      }
  import cats.effect.*
  import sttp.capabilities.fs2.Fs2Streams
  import sttp.client3.*
  import sttp.client3.http4s.*

  val http4sBackend: Resource[IO, SttpBackend[IO, Fs2Streams[IO]]] = Http4sBackend.usingDefaultBlazeClientBuilder[IO]()
  val sttpInterpreter: SttpClientInterpreter                       = SttpClientInterpreter()

  def createPoll(poll: PollCreate): IO[PollId] =
    http4sBackend.use { backend =>
      sttpInterpreter.toClientThrowErrors(PollEndpoints.createPoll, Some(baseSttpUri), backend)(
        WebSocketToPipe.webSocketsNotSupported
      )(poll)
    }

  def findPollByCode(code: SingleUseVoteCode): IO[Option[PollView]] =
    http4sBackend.use { backend =>
      sttpInterpreter.toClientThrowErrors(PollEndpoints.retrievePollByCode, Some(baseSttpUri), backend)(
        WebSocketToPipe.webSocketsNotSupported
      )(code)
    }

  def answerPoll(pollAnswer: PollAnswer): IO[Unit] =
    http4sBackend.use { backend =>
      sttpInterpreter.toClientThrowErrors(PollEndpoints.answerPoll, Some(baseSttpUri), backend)(
        WebSocketToPipe.webSocketsNotSupported
      )(pollAnswer)
    }

  def retrieveAnonymousResults(pollId: PollId): IO[List[AnsweredQuestionView]] =
    http4sBackend.use { backend =>
      sttpInterpreter.toClientThrowErrors(PollEndpoints.retrieveAnonymousResults, Some(baseSttpUri), backend)(
        WebSocketToPipe.webSocketsNotSupported
      )(pollId)
    }
