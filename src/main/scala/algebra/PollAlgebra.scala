package algebra

import entities.*
import cats.effect.IO
import entities.dto.{Answer, CreatePoll}

trait PollAlgebra:

  def create(poll: CreatePoll): IO[Unit]

  def answer(code: SingleUseVoteCode, answers: List[Answer]): IO[Unit]

  def retrieveAnonymousResults(pollId: PollId): IO[Unit]
