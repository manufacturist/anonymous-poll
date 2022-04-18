package page

import cats.effect.IO
import org.scalajs.dom.Element

trait Page:
  def render: Element
