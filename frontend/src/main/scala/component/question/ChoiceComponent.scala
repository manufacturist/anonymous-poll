package component.question

import component.Component
import entity.dto.QuestionView
import org.scalajs.dom.*
import scalatags.JsDom.all.*
import scalatags.JsDom.tags2.*

class ChoiceComponent(questionView: QuestionView) extends QuestionComponent:
  override def render: Element =
    div(`class` := "w-full py-5 px-3 mb-6 md:mb-0")(
      labelElement(questionView.text),
      select(
        `class` := "block appearance-none w-full bg-white border border-gray-400 hover:border-gray-500 px-4 py-2 pr-8 rounded shadow leading-tight focus:outline-none focus:shadow-outline"
      )(
        questionView.picks.map { text =>
          option(text)
        }
      )
    ).render
