package algebra

import entity.*
import cats.effect.IO
import entity.dto.*

trait PollAlgebra:

  def create(poll: PollCreate): IO[PollView]

  def findPollByCode(code: SingleUseVoteCode): IO[Option[PollView]]

  def answer(code: SingleUseVoteCode, answers: List[Answer]): IO[Unit]

  def retrieveAnonymousResults(pollId: PollId): IO[List[AnsweredQuestionView]]
