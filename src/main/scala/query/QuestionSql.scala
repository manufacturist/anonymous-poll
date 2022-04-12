package query

import db.Composites
import doobie.*
import doobie.implicits.*
import entity.*
import entity.dao.*
import entity.dto.QuestionView

object QuestionSql extends QuestionSql

trait QuestionSql:
  def createQuestions(questions: List[Question]): ConnectionIO[Int] =
    QuestionQueries.insertQuestion.updateMany(questions)

  def createAnswers(answers: List[Answer]): ConnectionIO[Int] =
    QuestionQueries.insertAnswer.updateMany(answers)

  def retrievePollAnswersByQuestions(pollId: PollId): ConnectionIO[Map[QuestionView, List[Answer]]] =
    QuestionQueries
      .selectAnswerWherePollId(eqPollId = pollId)
      .to[List]
      .map(_.groupMap { (questionView, answer) => questionView } { (_, answer) => answer })

private[query] object QuestionQueries extends Composites:
  val insertQuestion: Update[Question] =
    val sql = "INSERT INTO question (poll_id, number, text, type, picks, minimum, maximum) VALUES (?, ?, ?, ?, ?, ?, ?)"
    Update[Question](sql)

  val insertAnswer: Update[Answer] =
    val sql =
      """INSERT INTO answer (poll_id, question_number, email_address, answers, number) 
        |VALUES (?, ?, ?, ?, ?, ?, ?)""".stripMargin

    Update[Answer](sql)

  def selectAnswerWherePollId(eqPollId: PollId): Query0[(QuestionView, Answer)] =
    sql"""SELECT q.number, q.type, q.text, a.poll_id, a.question_number, a.email_address, a.answers, a.number 
         |FROM answer AS a
         |JOIN question AS q ON q.poll_id = a.poll_id
         |WHERE a.poll_id = $eqPollId""".stripMargin.query[(QuestionView, Answer)]
