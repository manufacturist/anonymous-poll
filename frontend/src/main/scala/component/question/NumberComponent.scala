package component.question

import entity.dto.QuestionView
import org.scalajs.dom.*
import scalatags.JsDom.all.*
import scalatags.JsDom.tags2.*

class NumberComponent(questionView: QuestionView) extends QuestionComponent:
  override def render: Element =
    div(`class` := "w-full py-5 px-3 mb-6 md:mb-0")(
      labelElement(questionView.text),
      input(
        `class` := "appearance-none block w-full bg-gray-200 text-gray-700 border rounded py-3 px-4 mb-3 leading-tight focus:outline-none focus:bg-white",
        `type` := "number",
        placeholder := ((questionView.min, questionView.max) match {
          case (Some(min), Some(max)) => s"[$min < $max]"
          case (Some(min), None)      => s"[$min < ...]"
          case (None, Some(max))      => s"[... < $max]"
          case (None, None)           => ""
        })
      )
    ).render
