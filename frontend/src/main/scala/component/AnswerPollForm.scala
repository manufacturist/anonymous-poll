package component

import component.*
import component.answer.QuestionComponent
import entity.QuestionType
import entity.dto.QuestionView
import i18n.{I18N, I18NSupport}
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
    )(I18NSupport.get(I18N.AnswerPoll.ANSWER_BTN)).render

    pollAnswerForm.appendChild(questionsWrapper)
    pollAnswerForm.appendChild(sendButton)
    pollAnswerForm
