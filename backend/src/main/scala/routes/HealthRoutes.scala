package routes

import cats.effect.IO
import endpoint.{HealthEndpoint, ServerEndpoint}

object HealthRoutes:
  def healthEndpoint: ServerEndpoint[Any, IO] =
    HealthEndpoint.health.serverLogic(_ => IO.pure(Right(())))
