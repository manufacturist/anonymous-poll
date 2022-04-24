package page

import cats.effect.IO
import cats.effect.unsafe.implicits.*
import client.PollApiClient
import component.*
import component.poll_create.*
import entity.*
import entity.dto.PollCreate
import org.scalajs.dom.*
import scalatags.JsDom.all.*

import scala.concurrent.duration.*

class CreatePollPage(pollApiClient: PollApiClient) extends Page:

  private val FORM_ID         = "create-poll-form"
  private val ADD_CHOICE_ID   = "add-choice"
  private val ADD_NUMBER_ID   = "add-number"
  private val ADD_OPEN_END_ID = "add-open-end"
  private val CREATE_POLL_ID  = "create-poll"

  override def renderElement: Element =
    val choiceButton     = baseButton(ADD_CHOICE_ID)("Add choice question")
    val numberButton     = baseButton(ADD_NUMBER_ID)("Add number question")
    val openEndButton    = baseButton(ADD_OPEN_END_ID)("Add open end question")
    val createPollButton = baseButton(CREATE_POLL_ID, "float-right" :: Nil)("Create poll")

    containerDiv(
      div(id := FORM_ID)(
        div(`class` := "grid grid-cols-4 gap-4 content-start")(
          div(`class` := "col-span-3 space-x-4")(
            choiceButton,
            numberButton,
            openEndButton
          ),
          div(`class` := "col-span-1")(
            createPollButton
          )
        ),
        div(id := NEW_QUESTIONS_ID)
      )
    ).render

  override def afterRender: IO[Unit] =
    IO.delay {
      document.getElementById(ADD_CHOICE_ID).asInstanceOf[HTMLButtonElement].onclick =
        _ => handleQuestionCreation(QuestionType.Choice)

      document.getElementById(ADD_NUMBER_ID).asInstanceOf[HTMLButtonElement].onclick =
        _ => handleQuestionCreation(QuestionType.Number)

      document.getElementById(ADD_OPEN_END_ID).asInstanceOf[HTMLButtonElement].onclick =
        _ => handleQuestionCreation(QuestionType.OpenEnd)

      document.getElementById(CREATE_POLL_ID).asInstanceOf[HTMLButtonElement].onclick =
        _ => handleCreatePollButtonClick()
    }

  private def handleQuestionCreation(questionType: QuestionType): Unit =
    IO.delay {
      val newQuestionsWrapper = document.getElementById(NEW_QUESTIONS_ID)

      val newQuestionNumber = QuestionNumber(
        // `.getAttribute` returns String | null in JavaScript
        Option(newQuestionsWrapper.getAttribute(QUESTION_COUNT_ATTR)).getOrElse("0").toInt + 1
      )

      newQuestionsWrapper.setAttribute(QUESTION_COUNT_ATTR, newQuestionNumber.toString)

      newQuestionsWrapper.append(CreateQuestionFactory(newQuestionNumber, questionType))
    }.unsafeRunAndForget()

  private def handleCreatePollButtonClick(): Unit =
    pollApiClient.createPoll(???).unsafeRunAndForget()
