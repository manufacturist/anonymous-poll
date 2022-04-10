package algebra

import entity.*
import cats.effect.IO
import entity.dto.{Answer, PollCreate}

trait PollAlgebra:

  def create(poll: PollCreate): IO[Unit]

  def answer(code: SingleUseVoteCode, answers: List[Answer]): IO[Unit]

  def retrieveAnonymousResults(pollId: PollId): IO[Unit]
