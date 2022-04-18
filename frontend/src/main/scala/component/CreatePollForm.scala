package component

import component.question.QuestionComponent
import entity.QuestionType
import entity.dto.QuestionView
import org.scalajs.dom.Element
import scalatags.JsDom.all.*

class CreatePollForm(questions: List[QuestionView]) extends Component:
  override def render: Element =
    val questionComponents: List[QuestionComponent] = questions.map(QuestionComponent.apply)
    val formHTML                                    = questionComponents.map(_.render.outerHTML).mkString("")

    val f = form().render
    f.innerHTML = formHTML
    f
