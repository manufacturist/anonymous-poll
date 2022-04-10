package algebra.impl

import config.*
import entities.*
import algebra.PollAlgebra
import cats.effect.IO
import entities.dto.{Answer, PollCreate}

class PollAlgebraImpl()(using config: AppConfig) extends PollAlgebra:

  override def create(poll: PollCreate): IO[Unit] = ???

  override def answer(code: SingleUseVoteCode, answers: List[Answer]): IO[Unit] = ???

  override def retrieveAnonymousResults(pollId: PollId): IO[Unit] = ???
