package component.poll_create

import component.*
import entity.*
import org.scalajs.dom.Element
import scalatags.JsDom.all.*

class OpenEndCreateComponent(questionNumber: QuestionNumber) extends Component {

  override def render: Element =
    questionWrapperElement(QuestionType.OpenEnd)
}
