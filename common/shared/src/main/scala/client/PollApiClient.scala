package client

import cats.effect.IO
import endpoint.{PollEndpoints, Response}
import entity.dto.{AnsweredQuestionView, PollAnswer, PollCreate, PollView}
import entity.{PollId, SingleUseVoteCode}
import org.http4s.client.Client
import org.http4s.{Request, Response, Uri}
import sttp.tapir.DecodeResult
import sttp.tapir.DecodeResult.*
import tapir.Http4sClientInterpreter

class PollApiClient(baseUri: Uri)(using Client[IO]):

  extension [E, O](httpIO: (Request[IO], Response[IO] => IO[DecodeResult[Either[E, O]]]))
    def run()(using client: Client[IO]) =
      val (request, parseResponse) = httpIO

      client.run(request).use {
        parseResponse(_).flatMap {
          case Value(Right(value))   => IO.pure(value)
          case Value(Left(unwanted)) => IO.raiseError(new RuntimeException(s"Got $unwanted"))
          case fail: Failure         => IO.raiseError(new RuntimeException(s"Couldn't decode due to $fail"))
        }
      }

  private val clientInterpreter = Http4sClientInterpreter[IO]()

  def createPoll(poll: PollCreate): IO[PollId] =
    clientInterpreter.toRequest(PollEndpoints.createPoll, Some(baseUri))(poll).run()

  def findPollByCode(code: SingleUseVoteCode): IO[Option[PollView]] =
    clientInterpreter.toRequest(PollEndpoints.retrievePollByCode, Some(baseUri))(code).run()

  def answerPoll(pollAnswer: PollAnswer): IO[Unit] =
    clientInterpreter.toRequest(PollEndpoints.answerPoll, Some(baseUri))(pollAnswer).run()

  def retrieveAnonymousResults(pollId: PollId): IO[List[AnsweredQuestionView]] =
    clientInterpreter.toRequest(PollEndpoints.retrieveAnonymousResults, Some(baseUri))(pollId).run()
