package page

import cats.effect.IO
import cats.effect.unsafe.implicits.*
import component.*
import component.create.CreateQuestionFactory
import entity.QuestionType
import org.scalajs.dom.*
import scalatags.JsDom.all.*

import scala.concurrent.duration.*

class CreatePollPage() extends Page:

  private val formId       = "create-poll-form"
  private val addChoiceId  = "add-choice"
  private val addNumberId  = "add-number"
  private val addOpenEndId = "add-open-end"

  override def renderElement: Element =
    val choiceButton  = baseButton(addChoiceId)("Add choice question")
    val numberButton  = baseButton(addNumberId)("Add number question")
    val openEndButton = baseButton(addOpenEndId)("Add open end question")

    containerDiv(
      form(id := formId)(
        choiceButton,
        numberButton,
        openEndButton
      )
    ).render

  override def afterRender: IO[Unit] =
    IO.delay {
      val form = document.getElementById(formId)

      document.getElementById(addChoiceId).asInstanceOf[HTMLButtonElement].onclick =
        _ => form.append(CreateQuestionFactory(QuestionType.Choice))

      document.getElementById(addNumberId).asInstanceOf[HTMLButtonElement].onclick =
        _ => form.append(CreateQuestionFactory(QuestionType.Number))

      document.getElementById(addOpenEndId).asInstanceOf[HTMLButtonElement].onclick =
        _ => form.append(CreateQuestionFactory(QuestionType.OpenEnd))
    }