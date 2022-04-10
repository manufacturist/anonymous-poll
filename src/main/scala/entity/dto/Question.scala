package entity.dto

import core.json.{*, given}
import entity.*

sealed trait Question:
  def text: Text

object Question:
  case class Choice(text: Text, answers: List[Text], isMultiPick: Boolean) extends Question
  case class Number(text: Text, min: Option[Int], max: Option[Int])        extends Question
  case class OpenEnd(text: Text)                                           extends Question

  given Codec[Question] = deriveCodec
