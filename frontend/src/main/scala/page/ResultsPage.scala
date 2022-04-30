package page

import cats.effect.*
import cats.effect.implicits.*
import cats.effect.unsafe.IORuntime
import cats.effect.unsafe.implicits.*
import client.PollApiClient
import component.*
import component.answer.*
import entity.*
import entity.dto.*
import i18n.*
import org.scalajs.dom.*
import scalatags.JsDom.all.*

import java.util.UUID
import scala.concurrent.Await
import scala.util.{Failure, Success, Try}

class ResultsPage(pollApiClient: PollApiClient) extends Page:

  private val POLL_ID_QUERY_PARAM = "pollId"
  private val CONTENT_ELEMENT_ID  = "content-element"

  private lazy val queryParams =
    new URLSearchParams(window.location.search)

  private lazy val (initialElement, pollIdOp): (Element, IO[PollId]) =
    Try(PollId(UUID.fromString(queryParams.get(POLL_ID_QUERY_PARAM)))) match {
      case Failure(exception) =>
        val element = containerDiv(
          p(I18NSupport.get(I18N.Home.IDEA_1))
        ).render

        (element, IO.raiseError(new RuntimeException("Couldn't read the pollId")))
      case Success(pollId) =>
        val element = containerDiv(
          div(`id` := CONTENT_ELEMENT_ID)(
            p("Loading poll results...")
          )
        ).render

        (element, IO.pure(pollId))
    }

  override def renderElement: Element =
    initialElement

  override def afterRender: IO[Unit] =
    for
      pollId                              <- pollIdOp
      results: List[AnsweredQuestionView] <- pollApiClient.retrieveAnonymousResults(pollId)
    yield {
      val resultsHTML = results
        .sortBy(_.number.value)
        .map {
          case AnsweredQuestionView.AnsweredChoiceView(number, text, results) =>
            div(
              labelQuestion(s"$number. $text"),
              table(
                tbody(
                  tr(
                    th(`class` := "text-left")(I18NSupport.get(I18N.ViewResults.RESPONSE)),
                    th(I18NSupport.get(I18N.ViewResults.VOTES))
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
              p(s"${I18NSupport.get(I18N.ViewResults.AVERAGE)} $average"),
              br()
            )

          case AnsweredQuestionView.AnsweredOpenEndView(number, text, answers) =>
            div(
              labelQuestion(s"$number. $text"),
              ol(
                answers.zipWithIndex.map { case (answer, index) =>
                  li(s"${index + 1}. $answer")
                }
              ),
              br()
            )
        }
        .map(_.render.innerHTML)
        .mkString

      document.getElementById(CONTENT_ELEMENT_ID).innerHTML = resultsHTML
    }
