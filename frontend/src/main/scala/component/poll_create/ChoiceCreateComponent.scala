package component.poll_create

import component.*
import entity.QuestionType
import entity.QuestionNumber
import org.scalajs.dom.{Element, console, document}
import scalatags.JsDom.all.*

class ChoiceCreateComponent(questionNumber: QuestionNumber) extends Component {

  val PICK_ADD_BTN_ID = s"pick-add-button-$questionNumber"
  val PICK_CLASS      = s"pick-for-$questionNumber"

  // TODO: Interesting. Prepending the same `val` doesn't work, however for `def` it does
  private def pickElement = input(
    `class`     := s"$PICK_CLASS $INPUT_CLASSES",
    placeholder := "Add pick..."
  ).render

  override def render: Element =
    // Text input + wrapper
    val wrapper = questionWrapperElement(QuestionType.Choice)

    // Minimum 2 picks
    wrapper.append(pickElement)
    wrapper.append(pickElement)

    // Add picks wrapper
    wrapper.append(
      baseButton(PICK_ADD_BTN_ID)(
        onclick := { () => document.getElementById(PICK_ADD_BTN_ID).before(pickElement) }
      )("+").render
    )

    wrapper
}
