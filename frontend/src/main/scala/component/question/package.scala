package component

import entity.Text
import org.scalajs.dom.Element
import scalatags.JsDom.all.*

package object question:

  val QUESTION_NUMBER_ATTRIBUTE = "question-number"
  val QUESTION_TYPE_ATTRIBUTE   = "question_type"

  def questionContainerDiv =
    div(`class` := "w-full py-5 px-3 mb-6 md:mb-0")
