package page

import cats.effect.*
import cats.effect.implicits.*
import cats.effect.unsafe.implicits.*
import cats.effect.unsafe.IORuntime
import client.PollApiClient
import component.*
import entity.SingleUseVoteCode
import entity.dto.PollView
import org.scalajs.dom.*
import scalatags.JsDom.all.*

import java.util.UUID
import scala.concurrent.Await
import scala.util.{Failure, Success, Try}

class AnswerPollPage(pollApiClient: PollApiClient) extends Page:

  private val codeParam = "code"

  private lazy val contentElement =
    document.getElementById("content-element")

  private lazy val queryParams =
    new URLSearchParams(window.location.search)

  private lazy val pollRetrieval: (Element, IO[SingleUseVoteCode]) =
    Try(SingleUseVoteCode(UUID.fromString(queryParams.get(codeParam)))) match {
      case Failure(exception) =>
        val element = container(
          p("⚠️ You are missing the vote code. Unable to perform poll retrieval")
        ).render

        (element, IO.raiseError(new RuntimeException("Couldn't read poll")))
      case Success(code) =>
        val element = container(
          p("Loading poll...")
        ).render

        (element, IO.pure(code))
    }

  override def render: Element =
    import concurrent.duration.DurationInt

    val (element, pollRetrievalOp) = pollRetrieval

    (for
      voteCode <- pollRetrievalOp
      pollView: Option[PollView] <- pollApiClient
        .findPollByCode(voteCode)
        .redeemWith(e => IO.println(s"sorry $e") *> IO.raiseError(e), _ => IO.pure(None))
    yield pollView match {
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
          p("Poll not found :(")
        ).render
    }).unsafeRunAndForget()

    element
