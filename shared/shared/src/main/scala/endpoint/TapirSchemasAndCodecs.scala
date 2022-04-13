package endpoint

import monix.newtypes.{HasBuilder, HasExtractor, TypeInfo}
import sttp.tapir.Codec.PlainCodec
import sttp.tapir.generic.SchemaDerivation
import sttp.tapir.{DecodeResult, Schema}

trait TapirSchemasAndCodecs extends SchemaDerivation:
  given [Base, New](using
    @unchecked builder: HasBuilder.Aux[New, Base],
    typeInfo: TypeInfo[New],
    schema: Schema[Base]
  ): Schema[New] =
    schema
      .copy(format = schema.format match {
        case None           => Option(typeInfo.asHumanReadable)
        case Some(original) => Option(s"$original(${typeInfo.asHumanReadable})")
      })
      .asInstanceOf[Schema[New]]

  given [Base, New](using
    builder: HasBuilder.Aux[New, Base],
    extractor: HasExtractor.Aux[New, Base],
    codec: PlainCodec[Base],
    schema: Schema[New]
  ): PlainCodec[New] =
    codec
      .mapDecode { base =>
        builder.build(base) match {
          case Right(value) => DecodeResult.Value(value)
          case Left(error)  => DecodeResult.Error(error.toReadableString, error.toException)
        }
      }(extractor.extract)
      .schema(schema)
