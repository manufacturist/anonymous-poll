package page

import cats.effect.IO
import cats.effect.unsafe.implicits.*
import client.PollApiClient
import component.*
import component.poll_create.*
import entity.*
import entity.dto.{PollCreate, PollRecipient, Question}
import org.scalajs.dom.{Text as _, *}
import scalatags.JsDom.all.*

import scala.concurrent.duration.*

class CreatePollPage(pollApiClient: PollApiClient) extends Page:

  private val FORM_ID         = "create-poll-form"
  private val POLL_NAME_ID    = "poll-name-id"
  private val RECIPIENT_ID    = "emails-weights-text-area"
  private val ADD_CHOICE_ID   = "add-choice"
  private val ADD_NUMBER_ID   = "add-number"
  private val ADD_OPEN_END_ID = "add-open-end"
  private val CREATE_POLL_ID  = "create-poll"

  private val emailRegex =
    """[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?""".r

  override def renderElement: Element =
    val choiceButton     = baseButton(ADD_CHOICE_ID)("Add choice question")
    val numberButton     = baseButton(ADD_NUMBER_ID)("Add number question")
    val openEndButton    = baseButton(ADD_OPEN_END_ID)("Add open end question")
    val createPollButton = baseButton(CREATE_POLL_ID, "float-right" :: Nil)("Create poll")

    containerDiv(
      div(id := FORM_ID)(
        div(`class` := "grid grid-cols-4 gap-4 content-start")(
          div(`class` := "col-span-4")(
            input(
              id          := POLL_NAME_ID,
              `class`     := INPUT_CLASSES,
              placeholder := "Poll name..."
            )
          ),
          div(`class` := "col-span-4")(
            textarea(
              id      := RECIPIENT_ID,
              `class` := TEXT_AREA_CLASSES,
              placeholder := "Specify emails separated by new lines:\nfoo@bar.baz\nwibble@wobble.wuuble\n\nOR specify emails & assigned vote weight:\n\nfoo@bar.baz,1.5\nwibble@wobble.wubble"
            )
          ),
          div(`class` := "col-span-3 space-x-4")(
            choiceButton,
            numberButton,
            openEndButton
          ),
          div(`class` := "col-span-1")(
            createPollButton
          )
        ),
        div(id := NEW_QUESTIONS_ID)
      )
    ).render

  override def afterRender: IO[Unit] =
    IO.delay {
      document.getElementById(ADD_CHOICE_ID).asInstanceOf[HTMLButtonElement].onclick =
        _ => handleAddQuestionButtonClick(QuestionType.Choice).unsafeRunAndForget()

      document.getElementById(ADD_NUMBER_ID).asInstanceOf[HTMLButtonElement].onclick =
        _ => handleAddQuestionButtonClick(QuestionType.Number).unsafeRunAndForget()

      document.getElementById(ADD_OPEN_END_ID).asInstanceOf[HTMLButtonElement].onclick =
        _ => handleAddQuestionButtonClick(QuestionType.OpenEnd).unsafeRunAndForget()

      document.getElementById(CREATE_POLL_ID).asInstanceOf[HTMLButtonElement].onclick =
        _ => handleCreatePollButtonClick().unsafeRunAndForget()
    }

  private def handleAddQuestionButtonClick(questionType: QuestionType): IO[Unit] =
    IO.delay {
      val newQuestionsWrapper = document.getElementById(NEW_QUESTIONS_ID)

      val newQuestionNumber = QuestionNumber(
        // `.getAttribute` returns String || null in JavaScript
        Option(newQuestionsWrapper.getAttribute(QUESTION_COUNT_ATTR)).getOrElse("0").toInt + 1
      )

      newQuestionsWrapper.setAttribute(QUESTION_COUNT_ATTR, newQuestionNumber.toString)

      newQuestionsWrapper.append(CreateQuestionFactory(newQuestionNumber, questionType))
    }

  private def handleCreatePollButtonClick(): IO[Unit] =
    for
      pollName <- IO.delay(PollName(document.getElementById(POLL_NAME_ID).asInstanceOf[HTMLInputElement].value))

      emails <- IO.delay {
        val recipientsText = document.getElementById(RECIPIENT_ID).asInstanceOf[HTMLTextAreaElement].value

        recipientsText
          .split('\n')
          .map(_.replaceAll("""\s""", ""))
          .filter(_.isBlank)
          .map {
            case line if line.contains(",") && line.split(",").length == 2 =>
              val split        = line.split(",")
              val emailAddress = EmailAddress(split.head)
              val voteWeight   = VoteWeight(split(1).toDouble)

              PollRecipient(emailAddress, Some(voteWeight))

            case emailRegex(emailAddress) =>
              PollRecipient(EmailAddress(emailAddress), None)

            case line =>
              throw new RuntimeException(s"Can't parse '$line' :(")
          }
          .toList
      }

      questions <- retrievePollQuestions

      _ <- pollApiClient.createPoll(
        PollCreate(
          name = pollName,
          recipients = emails,
          questions = questions
        )
      )
    yield ()

  private def retrievePollQuestions: IO[List[Question]] =
    IO.delay {
      val questionWrapperElements: List[Element] =
        document.querySelectorAll("""div[question-type]:not([value=""])""").toList

      val groupedByQuestionType: Map[QuestionType, List[List[Node]]] = questionWrapperElements
        .groupMap { element =>
          QuestionType.valueOf(element.getAttribute(QUESTION_TYPE_ATTRIBUTE))
        }(_.getElementsByClassName(QUESTION_CONTENT_CLASS).head.childNodes.toList)

      groupedByQuestionType.flatMap {
        case (QuestionType.Choice, nodeLists) =>
          nodeLists.map { nodeList =>
            // All except `add pick button`
            nodeList.dropRight(1) match {
              case (textArea: HTMLTextAreaElement) :: inputPicks if inputPicks.size > 1 =>
                val text  = Text(textArea.value)
                val picks = inputPicks.asInstanceOf[List[HTMLInputElement]].map(inputPick => Text(inputPick.value))
                Question.Choice(text, picks, isMultiPick = false)

              case other => throw new RuntimeException(s"Can't build Question.Choice with $other")
            }
          }

        case (QuestionType.Number, nodeLists) =>
          nodeLists.map {
            case (textArea: HTMLTextAreaElement) :: (minimumInput: HTMLInputElement) :: (maximumInput: HTMLInputElement) :: Nil =>
              val text    = Text(textArea.value)
              val minimum = if minimumInput.value == "" then None else Some(minimumInput.value.toInt)
              val maximum = if maximumInput.value == "" then None else Some(maximumInput.value.toInt)
              Question.Number(text, minimum, maximum)

            case other => throw new RuntimeException(s"Can't build Question.Number with $other")
          }

        case (QuestionType.OpenEnd, nodeLists) =>
          nodeLists.map {
            case (textArea: HTMLTextAreaElement) :: Nil => Question.OpenEnd(Text(textArea.value))
            case other => throw new RuntimeException(s"Can't build Question.OpenEnd with $other")
          }
      }.toList
    }
