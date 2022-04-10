package algebra

import entities.*
import cats.effect.IO
import entities.dto.{Answer, PollCreate}

trait PollAlgebra:

  def create(poll: PollCreate): IO[Unit]

  def answer(code: SingleUseVoteCode, answers: List[Answer]): IO[Unit]

  def retrieveAnonymousResults(pollId: PollId): IO[Unit]
