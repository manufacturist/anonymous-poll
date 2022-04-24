package component.poll_create

import component.*
import entity.*
import org.scalajs.dom.Element
import scalatags.JsDom.all.*

class NumberCreateComponent(questionNumber: QuestionNumber) extends Component {

  private val MIN_LIMIT = s"number-min-$questionNumber"
  private val MAX_LIMIT = s"number-max-$questionNumber"

  override def render: Element =
    val wrapper = questionWrapperElement(QuestionType.Number)
    wrapper.append(limitInput(Limit.Min))
    wrapper.append(limitInput(Limit.Max))
    wrapper

  private def limitInput(limit: Limit): Element =
    val placeholderText = limit match {
      case Limit.Min => "Lower inclusive limit..."
      case Limit.Max => "Upper inclusive limit..."
    }

    val limitClass = if limit == Limit.Max then MAX_LIMIT else MIN_LIMIT

    input(
      `class`     := s"$limitClass $INPUT_CLASSES",
      `type`      := "number",
      placeholder := placeholderText
    ).render

  private enum Limit:
    case Min, Max
}
