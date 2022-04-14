package client

import cats.effect.IO
import endpoint.{PollEndpoints, Response}
import entity.dto.{AnsweredQuestionView, PollAnswer, PollCreate, PollView}
import entity.{PollId, SingleUseVoteCode}
import org.http4s.client.Client
import org.http4s.{Request, Response, Uri}
import sttp.capabilities
import sttp.client3.SttpBackend
import sttp.client3.impl.cats.FetchCatsBackend
import sttp.model.Uri as SttpUri
import sttp.tapir.client.sttp.{SttpClientInterpreter, WebSocketToPipe}

import java.net.URI as JavaUri
import scala.concurrent.Future

final class PollApiClient(baseUri: Uri):

  private val baseSttpUri = SttpUri(JavaUri.create(baseUri.renderString))

  val fetchCatsBackend: SttpBackend[IO, capabilities.WebSockets] = FetchCatsBackend[IO]()
  val sttpInterpreter: SttpClientInterpreter                     = SttpClientInterpreter()

  def createPoll(poll: PollCreate): IO[PollId] =
    sttpInterpreter.toClientThrowErrors(PollEndpoints.createPoll, Some(baseSttpUri), fetchCatsBackend)(
      WebSocketToPipe.webSocketsNotSupported
    )(poll)

  def findPollByCode(code: SingleUseVoteCode): IO[Option[PollView]] =
    sttpInterpreter.toClientThrowErrors(PollEndpoints.retrievePollByCode, Some(baseSttpUri), fetchCatsBackend)(
      WebSocketToPipe.webSocketsNotSupported
    )(code)

  def answerPoll(pollAnswer: PollAnswer): IO[Unit] =
    sttpInterpreter.toClientThrowErrors(PollEndpoints.answerPoll, Some(baseSttpUri), fetchCatsBackend)(
      WebSocketToPipe.webSocketsNotSupported
    )(pollAnswer)

  def retrieveAnonymousResults(pollId: PollId): IO[List[AnsweredQuestionView]] =
    sttpInterpreter.toClientThrowErrors(PollEndpoints.retrieveAnonymousResults, Some(baseSttpUri), fetchCatsBackend)(
      WebSocketToPipe.webSocketsNotSupported
    )(pollId)
