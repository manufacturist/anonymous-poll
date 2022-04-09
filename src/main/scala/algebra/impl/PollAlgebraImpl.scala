package algebra.impl

import config.*
import domain.*
import algebra.PollAlgebra
import cats.effect.IO

class PollAlgebraImpl()(using config: AppConfig) extends PollAlgebra:

  override def createPoll(poll: CreatePoll): IO[Unit] = ???

  override def answerPoll(code: SingleUseVoteCode, answers: List[Answer]): IO[Unit] = ???

  override def retrieveAnonymousResults(pollId: PollId): IO[Unit] = ???