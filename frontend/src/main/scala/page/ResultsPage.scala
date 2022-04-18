package page

import cats.effect.*
import cats.effect.implicits.*
import cats.effect.unsafe.IORuntime
import cats.effect.unsafe.implicits.*
import client.PollApiClient
import component.*
import component.question.{QUESTION_NUMBER_ATTRIBUTE, QUESTION_TYPE_ATTRIBUTE}
import entity.*
import entity.dto.{Answer, AnsweredQuestionView, PollAnswer, PollView}
import org.scalajs.dom.{Text, *}
import scalatags.JsDom.all.*

import java.util.UUID
import scala.concurrent.Await
import scala.util.{Failure, Success, Try}

class ResultsPage(pollApiClient: PollApiClient) extends Page:

  private val pollIdParam      = "pollId"
  private val contentElementId = "content-element"

  private lazy val queryParams =
    new URLSearchParams(window.location.search)

  private lazy val pollIdParse: (Element, IO[PollId]) =
    Try(PollId(UUID.fromString(queryParams.get(pollIdParam)))) match {
      case Failure(exception) =>
        val element = containerDiv(
          p("⚠️ You are missing the poll id. Unable to view the results")
        ).render

        (element, IO.raiseError(new RuntimeException("Couldn't read the pollId")))
      case Success(pollId) =>
        val element = containerDiv(
          div(`id` := contentElementId)(
            p("Loading poll results...")
          )
        ).render

        (element, IO.pure(pollId))
    }

  override def render: Element =
    val (element, pollIdRead) = pollIdParse

    (for
      pollId                              <- pollIdRead
      results: List[AnsweredQuestionView] <- pollApiClient.retrieveAnonymousResults(pollId)

      _ = {
        val resultsHTML = results
          .sortBy(_.number.value)
          .map {
            case AnsweredQuestionView.AnsweredChoiceView(number, text, results) =>
              div(
                labelQuestion(s"$number. $text"),
                table(
                  tbody(
                    tr(
                      th("Response"),
                      th("Votes")
                    ),
                    results.toList.map { case (key, value) =>
                      tr(
                        td(key),
                        td(`class` := "float-right")(value.toString)
                      )
                    }
                  )
                ),
                br()
              )

            case AnsweredQuestionView.AnsweredNumberView(number, text, average) =>
              div(
                labelQuestion(s"$number. $text"),
                p(s"Average $average"),
                br()
              )

            case AnsweredQuestionView.AnsweredOpenEndView(number, text, answers) =>
              div(
                labelQuestion(s"$number. $text"),
                ol(
                  answers.zipWithIndex.map { case (answer, index) =>
                    li(s"$index. $answer")
                  }
                ),
                br()
              )
          }
          .map(_.render.innerHTML)
          .mkString

        document.getElementById(contentElementId).innerHTML = resultsHTML
      }
    yield ()).unsafeRunAndForget()

    element
