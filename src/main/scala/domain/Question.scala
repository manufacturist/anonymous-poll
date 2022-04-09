package domain

import core.json.{*, given}

sealed trait Question

object Question:
  case class Choice(text: Text, answers: List[Answer], isMultiPick: Boolean) extends Question
  case class Number(text: Text, min: Int, max: Int)                          extends Question
  case class OpenEnd(text: Text)                                             extends Question

  given Codec[Question] = deriveCodec
