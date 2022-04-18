import entity.Text
import org.scalajs.dom.Element
import scalatags.JsDom.all.*

package object component:

  val ANSWER_POLL_BUTTON_ID = "answer-poll"

  def labelQuestion(string: String) =
    tag("label")(
      `class` := "block uppercase tracking-wide text-gray-700 text-xs font-bold mb-2"
    )(string)

  def labelQuestionElement(string: String): Element =
    labelQuestion(string).render

  def baseButton(elementId: String, classes: List[String] = Nil) =
    button(
      id     := elementId,
      `type` := "button",
      `class` := "bg-white hover:bg-gray-100 text-gray-800 font-semibold py-2 px-4 border border-gray-400 rounded shadow " +
        classes.mkString(" ")
    )

  val containerDiv =
    div(`class` := "md:container md:mx-auto px-6 py-3")
