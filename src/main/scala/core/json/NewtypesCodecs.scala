package core.json

import io.circe.*
import monix.newtypes.*

trait NewtypesCodecs {

  given [New, Base](using extractor: HasExtractor.Aux[New, Base], encoder: Encoder[Base]): Encoder[New] =
    encoder.contramap(extractor.extract)

  // Inlined from https://github.com/monix/newtypes/blob/main/integration-circe/all/shared/src/main/scala/monix/newtypes/integrations/DerivedCirceCodec.scala
  // Great usage of java SAM interface for the decoder :clap:
  given [New, Base](using HasBuilder.Aux[New, Base], Decoder[Base]): Decoder[New] =
    jsonDecode(_)

  protected def jsonDecode[T, S](
    c: HCursor
  )(implicit builder: HasBuilder.Aux[T, S], dec: Decoder[S]): Decoder.Result[T] =
    dec.apply(c).flatMap { value =>
      builder.build(value) match {
        case value @ Right(_) =>
          value.asInstanceOf[Either[DecodingFailure, T]]
        case Left(failure) =>
          val msg = failure.message.fold("")(m => s" — $m")
          Left(
            DecodingFailure(
              s"Invalid ${failure.typeInfo.typeLabel}$msg",
              c.history
            )
          )
      }
    }
}
