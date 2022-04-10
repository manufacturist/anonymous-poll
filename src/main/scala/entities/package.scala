import monix.newtypes.NewsubtypeWrapped
import java.util.UUID
import scala.quoted.Varargs

package object entities:

  object PollId extends NewsubtypeWrapped[UUID]
  type PollId = PollId.Type

  object PollName extends NewsubtypeWrapped[String]
  type PollName = PollName.Type

  object QuestionId extends NewsubtypeWrapped[UUID]
  type QuestionId = QuestionId.Type

  enum QuestionType:
    case Choice, Number, OpenEnd

  object Text extends NewsubtypeWrapped[String]
  type Text = Text.Type

  object Password extends NewsubtypeWrapped[String]
  type Password = Password.Type

  object EmailAddress extends NewsubtypeWrapped[String]
  type EmailAddress = EmailAddress.Type

  object EmailSubject extends NewsubtypeWrapped[String]
  type EmailSubject = EmailSubject.Type

  object EmailContent extends NewsubtypeWrapped[String]
  type EmailContent = EmailContent.Type

  type Template = Template.Type
  object Template extends NewsubtypeWrapped[String] {
    extension (template: Template) def render(elements: List[Any]): String = template.format(elements*)
  }

  object SubjectTemplate extends NewsubtypeWrapped[Template]
  type SubjectTemplate = SubjectTemplate.Type

  object ContentTemplate extends NewsubtypeWrapped[Template]
  type ContentTemplate = ContentTemplate.Type

  object SingleUseVoteCode extends NewsubtypeWrapped[String]
  type SingleUseVoteCode = SingleUseVoteCode.Type
