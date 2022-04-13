import org.typelevel.log4cats.SelfAwareStructuredLogger
import cats.effect.IO

package object core:
  type Logger = SelfAwareStructuredLogger[IO]
