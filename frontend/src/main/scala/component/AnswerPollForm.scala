package component

import component.*
import component.question.QuestionComponent
import entity.QuestionType
import entity.dto.QuestionView
import org.scalajs.dom.Element
import scalatags.JsDom.all.*

class AnswerPollForm(questions: List[QuestionView]) extends Component:
  override def render: Element =
    val questionComponents: List[QuestionComponent] = questions.map(QuestionComponent.apply)
    val questionsHTML                               = questionComponents.map(_.render.outerHTML).mkString("")

    val pollAnswerForm = form().render

    val questionsWrapper = div(`class` := "border-solid border-gray-400 border rounded").render
    questionsWrapper.innerHTML = questionsHTML

    val sendButton = baseButton(
      elementId = ANSWER_POLL_BUTTON_ID,
      classes = List("my-2 float-right")
    )("Register answers").render

    pollAnswerForm.appendChild(questionsWrapper)
    pollAnswerForm.appendChild(sendButton)
    pollAnswerForm
