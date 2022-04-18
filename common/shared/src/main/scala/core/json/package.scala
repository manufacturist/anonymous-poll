package core

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import scala.deriving.Mirror

package object json extends NewtypesCodecs:

  type Codec[A] = io.circe.Codec[A]

  inline def deriveCodec[A](using inline A: Mirror.Of[A]): Codec.AsObject[A] = Codec.AsObject.derived[A]
