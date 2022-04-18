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

  private val codeParam        = "code"
  private val contentElementId = "content-element"

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
          div(`id` := contentElementId)(
            p("Loading poll...")
          )
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
        .redeemWith(e => IO.println(s"sorry $e") *> IO.raiseError(e), IO.pure)
    yield {
      val updatedElement = pollView match {
        case Some(pollView) =>
          val formWrapper = container().render
          formWrapper.appendChild(
            h2(`class` := "font-medium leading-tight text-4xl mt-0 mb-2")(s"\"${pollView.name}\" poll").render
          )
          formWrapper.appendChild(new PollForm(pollView.questions).render)
          formWrapper.appendChild(
            p(
              "Results can be viewed ",
              a(href := s"./?pollId=${pollView.id}#Results", `class` := "font-bold text-blue-400")("here")
            ).render
          )
          formWrapper
        case None =>
          container(
            p("Poll not found :(")
          ).render
      }

      document.getElementById(contentElementId).innerHTML = updatedElement.innerHTML
    }).unsafeRunAndForget()

    element
