package component.poll_create

import component.*
import entity.*
import org.scalajs.dom.{Element, document}
import scalatags.JsDom.all.*

object CreateQuestionFactory {

  def apply(questionNumber: QuestionNumber, questionType: QuestionType): Element =
    val questionElement = questionType match {
      case QuestionType.Choice  => new ChoiceCreateComponent(questionNumber).render
      case QuestionType.Number  => new NumberCreateComponent(questionNumber).render
      case QuestionType.OpenEnd => new OpenEndCreateComponent(questionNumber).render
    }

    val questionWrapperId = s"new-question-$questionNumber-wrapper"

    val questionWrapper = {
      val separator = div(`class` := "relative flex py-2 items-center")(
        div(`class` := "flex-grow border-t border-gray-400"),
        span(`class` := s"$QUESTION_SEPARATOR_CLASS flex-shrink mx-4 text-gray-400")(questionNumber.toString),
        div(`class` := "flex-grow border-t border-gray-400")
      ).render

      // I feel dirty writing such code
      val deleteButton = baseButton()(
        onclick := { () =>
          document.getElementById(questionWrapperId).remove()
          recalculateSeparatorNumbers()
        }
      )("Delete question").render

      val wrapper = div(
        id                            := questionWrapperId,
        attr(QUESTION_TYPE_ATTRIBUTE) := s"$questionType"
      ).render

      wrapper.append(separator)
      wrapper.append(questionElement)
      wrapper.append(deleteButton)
      wrapper
    }

    questionWrapper

  private def recalculateSeparatorNumbers(): Unit =
    val spanElements: List[(Element, Int)] =
      document.getElementsByClassName(QUESTION_SEPARATOR_CLASS).toList.zipWithIndex

    spanElements.foreach { case (element, index) =>
      element.innerText = (index + 1).toString
    }

    document.getElementById(NEW_QUESTIONS_ID).setAttribute(QUESTION_COUNT_ATTR, spanElements.size.toString)
}
