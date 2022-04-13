package main

import entity.dto.{PollCreate, Question}
import entity.{EmailAddress, PollName, Text}

object Fixtures:

  // Inspired from https://typelevel.org/blog/2022/04/01/call-for-steering-committee-members.html
  val choiceQuestion: Question.Choice = Question.Choice(
    text = Text("Which is the most important responsibility?"),
    answers = List(
      Text("Bring something new that's currently missing from the table"),
      Text("Participate in governance discussions & reimagine the way it runs"),
      Text("Improve the current charter")
    ),
    isMultiPick = false
  )

  val fooEmailAddress: EmailAddress = EmailAddress("foo@bar.com")

  val pollCreate: PollCreate = PollCreate(
    name = PollName("Typelevel Steering Committee Members"),
    recipients = Set(fooEmailAddress, EmailAddress("baz@qux.com")),
    questions = List(
      choiceQuestion,
      Question.Number(
        text = Text("How long have you been using Scala 3 (months)?"),
        min = Some(0),
        max = None
      ),
      Question.OpenEnd(
        text = Text(
          "If you were to recommend any functional Scala framework to a friend, " +
            "how long would it take him to learn CE3?"
        )
      )
    )
  )
