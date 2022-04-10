package algebra.impl

import config.*
import entities.*
import algebra.PollAlgebra
import cats.effect.IO
import entities.dto.{Answer, CreatePoll}

class PollAlgebraImpl()(using config: AppConfig) extends PollAlgebra:

  override def create(poll: CreatePoll): IO[Unit] = ???

  override def answer(code: SingleUseVoteCode, answers: List[Answer]): IO[Unit] = ???

  override def retrieveAnonymousResults(pollId: PollId): IO[Unit] = ???
