package component.answer

import component.Component
import entity.QuestionType
import entity.dto.QuestionView

trait QuestionComponent extends Component

object QuestionComponent:
  def apply(questionView: QuestionView): QuestionComponent =
    questionView.`type` match {
      case QuestionType.Choice  => new ChoiceComponent(questionView)
      case QuestionType.Number  => new NumberComponent(questionView)
      case QuestionType.OpenEnd => new OpenEndComponent(questionView)
    }
