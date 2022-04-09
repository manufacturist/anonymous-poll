import ciris.*
import monix.newtypes.HasBuilder

package object config:
  given [Base, New](using
    builder: HasBuilder.Aux[New, Base],
    decoder: ConfigDecoder[String, Base]
  ): ConfigDecoder[String, New] =
    decoder.mapEither { (maybeConfigKey, base) =>
      builder.build(base).left.map(buildFailure => ConfigError(buildFailure.toReadableString))
    }
