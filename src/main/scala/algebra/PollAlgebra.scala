package algebra

import domain.*
import cats.effect.IO

trait PollAlgebra:

  def createPoll(poll: CreatePoll): IO[Unit]

  def answerPoll(code: SingleUseVoteCode, answers: List[Answer]): IO[Unit]

  def retrieveAnonymousResults(pollId: PollId): IO[Unit]
