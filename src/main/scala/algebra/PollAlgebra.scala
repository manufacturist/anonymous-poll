package algebra

import domain.*
import cats.effect.IO
import domain.dto.{Answer, CreatePoll}

trait PollAlgebra:

  def create(poll: CreatePoll): IO[Unit]

  def answer(code: SingleUseVoteCode, answers: List[Answer]): IO[Unit]

  def retrieveAnonymousResults(pollId: PollId): IO[Unit]
