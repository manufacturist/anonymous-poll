package component.question

import component.Component
import entity.dto.QuestionView
import org.scalajs.dom.*
import scalatags.JsDom.all.*
import scalatags.JsDom.tags2.*

class OpenEndComponent(questionView: QuestionView) extends QuestionComponent:
  override def render: Element =
    div(`class` := "w-full py-5 px-3 mb-6 md:mb-0")(
      labelElement(questionView.text),
      input(
        `class` := "appearance-none block w-full bg-gray-200 text-gray-700 border rounded py-3 px-4 mb-3 leading-tight focus:outline-none focus:bg-white",
        `type`      := "text",
        placeholder := "Your opinionated answer goes here..."
      )
    ).render
