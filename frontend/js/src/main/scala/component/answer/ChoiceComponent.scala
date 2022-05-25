package component.answer

import component.*
import entity.dto.QuestionView
import org.scalajs.dom.*
import scalatags.JsDom.all.*

class ChoiceComponent(questionView: QuestionView) extends QuestionComponent:
  override def render: Element =
    questionContainerDiv(
      labelQuestionElement(questionView.display),
      select(
        attr(QUESTION_NUMBER_ATTRIBUTE) := questionView.number.toString,
        attr(QUESTION_TYPE_ATTRIBUTE)   := questionView.`type`.toString,
        `class` := "block appearance-none w-full bg-white border border-gray-400 hover:border-gray-500 px-4 py-2 pr-8 rounded shadow leading-tight focus:outline-none focus:shadow-outline"
      )(
        questionView.picks.map { text =>
          option(text)
        }
      )
    ).render
