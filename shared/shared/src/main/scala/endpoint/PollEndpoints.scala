package endpoint

import core.json.{*, given}
import entity.*
import entity.dto.*

object PollEndpoints:
  private val version      = 1
  private val baseEndpoint = endpoint.prependIn("api" / s"v$version")

  val createPoll: Endpoint[Unit, PollCreate, Unit, PollId, Any] =
    baseEndpoint.post
      .name("Create poll")
      .description(
        "Creates a **poll** and notifies recipients via email (*fire-and-forget manner*). " +
          "The returned poll id can be used to retrieve the **results**"
      )
      .in("poll")
      .in(jsonBody[PollCreate])
      .out(jsonBody[PollId])
      .out(statusCode(StatusCode.Ok))

  val retrievePollByCode: Endpoint[Unit, SingleUseVoteCode, Unit, Option[PollView], Any] =
    baseEndpoint.get
      .name("Retrieve poll via code")
      .description(
        "Will return a poll view (*id, name, questions*) for the " +
          "specified single use vote code, if it exists"
      )
      .in("poll" / query[SingleUseVoteCode]("code"))
      .out(jsonBody[Option[PollView]])
      .out(statusCode(StatusCode.Ok))

  val answerPoll: Endpoint[Unit, PollAnswer, Unit, Unit, Any] =
    baseEndpoint.post
      .name("Answer poll questions")
      .description("Registers the user's answers to a poll. Once registered, the vote code is no longer available")
      .in("poll" / "answer")
      .in(jsonBody[PollAnswer])
      .out(emptyOutput)
      .out(statusCode(StatusCode.NoContent))

  val retrieveAnonymousResults: Endpoint[Unit, PollId, Unit, List[AnsweredQuestionView], Any] =
    baseEndpoint.get
      .name("Retrieve poll results")
      .description("Returns accumulated results from the current registered answers")
      .in("poll" / path[PollId] / "results")
      .out(jsonBody[List[AnsweredQuestionView]])
      .out(statusCode(StatusCode.Ok))
