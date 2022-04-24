package component

import component.*
import entity.*
import org.scalajs.dom.Element
import scalatags.JsDom.all.*

package object poll_create:
  val QUESTION_COUNT_ATTR: String      = "question-count"
  val QUESTION_SEPARATOR_CLASS: String = "question-separator-value"
  val NEW_QUESTIONS_ID: String         = "new-questions"

  def questionWrapperElement(questionType: QuestionType): Element =
    val placeholderText = questionType match {
      case QuestionType.Choice  => "Your choice question..."
      case QuestionType.Number  => "Your number question..."
      case QuestionType.OpenEnd => "Your open-ended question..."
    }

    div(
      textarea(
        `class`     := TEXT_AREA_CLASSES,
        placeholder := placeholderText
      )
    ).render
