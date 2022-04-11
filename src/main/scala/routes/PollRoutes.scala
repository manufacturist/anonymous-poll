package routes

import algebra.PollAlgebra
import cats.effect.IO
import endpoint.*

final class PollRoutes(pollAlgebra: PollAlgebra):

  def serverEndpoints: List[ServerEndpoint[Any, IO]] =
    List(
      PollEndpoints.createPoll.serverLogic[IO] { pollCreate =>
        pollAlgebra.create(pollCreate).map(pollView => Right(pollView.id))
      },
      PollEndpoints.retrievePollByCode.serverLogic[IO] { code =>
        pollAlgebra.findPollByCode(code).map(Right(_))
      },
      PollEndpoints.answerPoll.serverLogic[IO] { pollAnswer =>
        pollAlgebra.answer(pollAnswer.code, pollAnswer.answers).map(Right(_))
      },
      PollEndpoints.retrieveAnonymousResults.serverLogic[IO] { pollId =>
        pollAlgebra.retrieveAnonymousResults(pollId).map(Right(_))
      }
    )
