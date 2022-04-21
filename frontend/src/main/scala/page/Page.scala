package page

import cats.effect.IO
import org.scalajs.dom.Element

trait Page:
  def renderElement: Element
  def afterRender: IO[Unit] = IO.unit
