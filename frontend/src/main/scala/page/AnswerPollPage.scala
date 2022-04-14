package page

import cats.effect.*
import cats.effect.implicits.*
import cats.effect.unsafe.IORuntime
import client.PollApiClient
import component.*
import entity.SingleUseVoteCode
import entity.dto.PollView
import org.scalajs.dom.*
import scalatags.JsDom.all.*
import org.http4s.client.Client as Http4sClient

import java.util.UUID
import scala.concurrent.Await
import scala.util.{Failure, Success, Try}

class AnswerPollPage(pollApiClient: PollApiClient)(using Http4sClient[IO], IORuntime) extends Page:

  private val codeParam = "code"

  private lazy val queryParams =
    new URLSearchParams(window.location.search)

  private lazy val pollRetrieval: IO[Option[PollView]] =
    Try(UUID.fromString(queryParams.get(codeParam))) match {
      case Failure(exception) =>
        IO.pure(None)
      case Success(code) =>
        pollApiClient.findPollByCode(SingleUseVoteCode(code))
    }

  override def render: Element =
    import concurrent.duration.DurationInt

    pollRetrieval
      .map {
        case Some(pollView) =>
          container(
            h3(s"Poll ${pollView.name}"),
            pollView.questions.map(q => p(q.toString)),
            p(
              "Results can be viewed ",
              a(href := s"./?pollId=${pollView.id}#Results")("here")
            )
          ).render
        case None =>
          container(
            p("⚠️ You are missing the vote code. Unable to retrieve a poll")
          ).render
      }
      .syncStep // IO to SyncIO due to https://typelevel.org/cats-effect/docs/2.x/datatypes/syncio (second paragraph)
      .unsafeRunSync()
      .getOrElse(throw new RuntimeException("Failed to render element"))
