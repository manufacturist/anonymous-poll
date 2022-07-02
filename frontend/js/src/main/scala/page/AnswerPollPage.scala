package page

import cats.effect.*
import cats.effect.implicits.*
import cats.effect.unsafe.implicits.*
import client.PollApiClient
import component.*
import component.answer.*
import entity.*
import entity.dto.{Answer, PollAnswer, PollView}
import i18n.{I18N, I18NSupport}
import org.scalajs.dom.{Element, MouseEvent, NodeList, URLSearchParams, document, html, window}
import scalatags.JsDom.all.*

import java.util.UUID
import scala.concurrent.Await
import scala.util.{Failure, Success, Try}

class AnswerPollPage(pollApiClient: PollApiClient) extends Page:

  private val CODE_QUERY_PARAM   = "code"
  private val CONTENT_ELEMENT_ID = "content-element"

  private lazy val queryParams =
    new URLSearchParams(window.location.search)

  private lazy val (initialElement, voteCodeOp): (Element, IO[SingleUseVoteCode]) =
    Try(SingleUseVoteCode(UUID.fromString(queryParams.get(CODE_QUERY_PARAM)))) match {
      case Failure(exception) =>
        val element = containerDiv(p(I18NSupport.get(I18N.AnswerPoll.MISSING_CODE))).render
        (element, IO.raiseError(new RuntimeException("Couldn't read poll", exception)))
      case Success(code) =>
        val element = containerDiv(div(`id` := CONTENT_ELEMENT_ID)(p("Loading poll..."))).render
        (element, IO.pure(code))
    }

  override def renderElement: Element =
    initialElement

  override def afterRender: IO[Unit] =
    for
      voteCode                   <- voteCodeOp
      pollView: Option[PollView] <- pollApiClient.findPollByCode(voteCode).redeemWith(IO.raiseError, IO.pure)
    yield {
      val (updatedElement, maybePollView) = pollView match {
        case Some(pollView) =>
          val formWrapper = containerDiv().render

          println(s"${I18NSupport.get(I18N.AnswerPoll.TITLE)} - ${pollView.name}")

          formWrapper.appendChild(
            h2(`class` := "font-medium leading-tight text-4xl mt-0 mb-2")(
              String.format(I18NSupport.get(I18N.AnswerPoll.TITLE), pollView.name)
            ).render
          )

          formWrapper.appendChild(new AnswerPollForm(pollView.questions).render)
          formWrapper.appendChild(br().render)

          val resultsHereParagraph = p(I18NSupport.get(I18N.AnswerPoll.RESULTS_HERE)).render
          resultsHereParagraph.appendChild(resultAnchor(pollView.id)(I18NSupport.get(I18N.AnswerPoll.HERE)).render)

          formWrapper.appendChild(resultsHereParagraph)

          (formWrapper, Some(pollView))
        case None =>
          val notFound = containerDiv(p("Poll not found :(")).render
          (notFound, None)
      }

      document.getElementById(CONTENT_ELEMENT_ID).innerHTML = updatedElement.innerHTML

      maybePollView.foreach { pollView =>
        document.getElementById(ANSWER_POLL_BUTTON_ID).asInstanceOf[html.Button].onclick =
          _ => handleAnswerPollButtonClick(voteCode, pollView.id)
      }
    }

  private def handleAnswerPollButtonClick(voteCode: SingleUseVoteCode, pollId: PollId): Unit =
    val answerFields: List[html.Input | html.Select] = {
      val inputs = document
        .querySelectorAll("""input[question-number]:not([value=""])""")
        .toList
        .map(_.asInstanceOf[html.Input])

      val selects: List[html.Select] = document
        .querySelectorAll("""select[question-number]:not([value=""])""")
        .toList
        .map(_.asInstanceOf[html.Select])

      inputs.concat(selects)
    }

    val answers: List[Answer] = answerFields.map { input =>
      val questionType   = QuestionType.valueOf(input.getAttribute(QUESTION_TYPE_ATTRIBUTE))
      val questionNumber = QuestionNumber(input.getAttribute(QUESTION_NUMBER_ATTRIBUTE).toInt)

      (input: @unchecked) match {
        case input: html.Input if questionType == QuestionType.Number =>
          Answer.Number(questionNumber, input.value.toInt)

        case input: html.Input if questionType == QuestionType.OpenEnd =>
          Answer.OpenEnd(questionNumber, Text(input.value))

        case select: html.Select if questionType == QuestionType.Choice =>
          Answer.Choice(questionNumber, Text(select.value) :: Nil)
      }
    }

    val pollAnswer = PollAnswer(
      code = voteCode,
      answers = answers
    )

    pollApiClient
      .answerPoll(pollAnswer)
      .map(_ =>
        document.getElementById(CONTENT_ELEMENT_ID).innerHTML = p(
          I18NSupport.get(I18N.AnswerPoll.ANSWERED),
          resultAnchor(pollId)(I18NSupport.get(I18N.AnswerPoll.HERE))
        ).render.innerHTML
      )
      .unsafeRunAndForget()

  private def resultAnchor(pollId: PollId) = a(
    `class` := "font-bold text-blue-800 hover:text-blue-200",
    href    := s"./?pollId=$pollId#Results"
  )
