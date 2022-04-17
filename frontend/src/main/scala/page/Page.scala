package page

import cats.effect.IO
import org.scalajs.dom.Element

trait Page:
  // If parent present, element will render within parent
  def parent: Option[Element] = None
  def render: Element
