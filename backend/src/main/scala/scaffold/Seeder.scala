package scaffold

import algebra.PollAlgebra
import core.Logger
import client.PollApiClient
import org.http4s.client.Client
import cats.effect.*
import config.Environment
import entity.dto.{PollCreate, Question}
import entity.{EmailAddress, PollName, Text}

final class Seeder(pollAlgebra: PollAlgebra)(using Logger) {

  private val logger = summon[Logger]

  def seed(): IO[Unit] =
    for
      _ <- logger.info("\uD83C\uDF31 Seeding started...")

      // Taken from the ITs fixtures
      pollCreate: PollCreate = PollCreate(
        name = PollName("Typelevel Steering Committee Members"),
        recipients = Set(
          EmailAddress("scala1@sca.la"),
          EmailAddress("scala2@sca.la"),
          EmailAddress("scala3@sca.la")
        ),
        questions = List(
          Question.Choice(
            text = Text("Which is the most important responsibility?"),
            answers = List(
              Text("Bring something new to the table (currently missing)"),
              Text("Participate in governance discussions & reimagine the way it runs"),
              Text("Improve the current charter")
            ),
            isMultiPick = false
          ),
          Question.Number(
            text = Text("How long have you been using Scala 3 (months)?"),
            min = Some(0),
            max = None
          ),
          Question.OpenEnd(
            text = Text(
              "If you were to recommend any functional Scala framework to a friend, " +
                "how long would it take him / her / them to learn CE3?"
            )
          )
        )
      )

      pollView <- pollAlgebra.create(pollCreate)
      _        <- logger.info(s"Poll ${pollView.id} created")

      _ <- logger.info("\uD83C\uDF31 Seeding ended successfully...")
    yield ()
}

object Seeder {

  def apply(env: Environment, pollAlgebra: PollAlgebra)(using Logger): Resource[IO, Unit] =
    env match {
      case Environment.LOCAL => Resource.eval(new Seeder(pollAlgebra).seed())
      case _                 => Resource.eval(summon[Logger].info(s"Seeding not available for $env") *> IO.unit)
    }
}
