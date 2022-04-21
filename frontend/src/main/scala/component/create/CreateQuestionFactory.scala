package component.create

import component.Component
import entity.QuestionType
import org.scalajs.dom.Element

object CreateQuestionFactory {
  
  def apply(questionType: QuestionType): Element = questionType match {
    case QuestionType.Choice  => (new ChoiceCreateComponent).render
    case QuestionType.Number  => (new NumberCreateComponent).render
    case QuestionType.OpenEnd => (new OpenEndCreateComponent).render
  }
}
