package query

import entity.*
import entity.dao.{Answer, Question}

import java.time.OffsetDateTime
import java.util.UUID

final class QuestionSqlSpec() extends TransactorFixtureSuite:

  test("insertQuestion (Choice)") {
    val question = Question(
      pollId = pollId,
      number = QuestionNumber(0),
      text = Text("Is it night or day?"),
      `type` = QuestionType.Choice,
      picks = Text("night") :: Text("day") :: Nil,
      minimum = None,
      maximum = None
    )

    check(QuestionQueries.insertQuestion.toUpdate0(question))
  }

  test("insertQuestion (Choice)") {
    val question = Question(
      pollId = pollId,
      number = QuestionNumber(0),
      text = Text("Quel âge as-tu?"),
      `type` = QuestionType.Number,
      picks = Nil,
      minimum = Some(0),
      maximum = Some(140)
    )

    check(QuestionQueries.insertQuestion.toUpdate0(question))
  }

  test("insertAnswer (Choice)") {
    val answer = Answer(
      pollId = pollId,
      questionNumber = QuestionNumber(0),
      email = EmailAddress("foo@bar.com"),
      answers = Text("day") :: Nil,
      number = None
    )

    check(QuestionQueries.insertAnswer.toUpdate0(answer))
  }

  test("insertAnswer (Number)") {
    val answer = Answer(
      pollId = pollId,
      questionNumber = QuestionNumber(0),
      email = EmailAddress("foo@bar.com"),
      answers = Nil,
      number = Some(100)
    )

    check(QuestionQueries.insertAnswer.toUpdate0(answer))
  }

  test("selectAnswerWherePollId") {
    check(QuestionQueries.selectAnswerWherePollId(pollId))
  }
