package component.poll_create

import component.*
import entity.QuestionType
import entity.QuestionNumber
import org.scalajs.dom.{Element, console, document}
import scalatags.JsDom.all.*

class ChoiceCreateComponent(questionNumber: QuestionNumber) extends Component {

  val PICK_WRAPPER_ID = s"picks-wrapper-$questionNumber"
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

    val picksWrapper = div(id := PICK_WRAPPER_ID)(
      baseButton(PICK_ADD_BTN_ID)(
        onclick := { () => document.getElementById(PICK_ADD_BTN_ID).before(pickElement) }
      )("+")
    ).render

    // Minimum 2 picks
    picksWrapper.prepend(pickElement)
    picksWrapper.prepend(pickElement)

    // Add picks wrapper
    wrapper.append(picksWrapper)

    wrapper
}
