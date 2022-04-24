import entity.Text
import org.scalajs.dom.Element
import scalatags.JsDom.all.*

package object component:

  val ANSWER_POLL_BUTTON_ID = "answer-poll"

  val INPUT_CLASSES: String =
    """px-3 py-1.5 mr-3 my-1.5 text-base font-normal text-gray-700 bg-white bg-clip-padding border border-solid border-gray-300 rounded m-0 focus:text-gray-700 focus:bg-white focus:border-blue-600 focus:outline-none"""

  val TEXT_AREA_CLASSES: String =
    """block form-control w-full px-3 py-1.5 mr-3 text-base font-normal text-gray-700 bg-white bg-clip-padding border border-solid border-gray-300 rounded m-0 focus:text-gray-700 focus:bg-white focus:border-blue-600 focus:outline-none"""

  val containerDiv = div(`class` := "md:container md:mx-auto px-6 py-3")

  def labelQuestionElement(string: String): Element =
    labelQuestion(string).render

  def labelQuestion(string: String) =
    tag("label")(
      `class` := "block uppercase tracking-wide text-gray-700 text-xs font-bold mb-2"
    )(string)

  def baseButton(elementId: String = "", classes: List[String] = Nil) =
    button(
      id     := elementId,
      `type` := "button",
      `class` := "bg-white hover:bg-gray-100 text-gray-800 font-semibold my-1.5 py-1.5 px-4 border border-gray-400 rounded shadow " +
        classes.mkString(" ")
    )
