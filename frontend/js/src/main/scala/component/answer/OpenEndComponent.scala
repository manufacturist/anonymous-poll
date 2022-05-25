package component.answer

import component.*
import entity.dto.QuestionView
import org.scalajs.dom.*
import scalatags.JsDom.all.*

class OpenEndComponent(questionView: QuestionView) extends QuestionComponent:
  override def render: Element =
    questionContainerDiv(
      labelQuestionElement(questionView.display),
      input(
        attr(QUESTION_NUMBER_ATTRIBUTE) := questionView.number.toString,
        attr(QUESTION_TYPE_ATTRIBUTE)   := questionView.`type`.toString,
        `class` := "appearance-none block w-full bg-gray-200 text-gray-700 border rounded py-3 px-4 mb-3 leading-tight focus:outline-none focus:bg-white",
        `type`      := "text",
        placeholder := "Your opinionated answer goes here..."
      )
    ).render
