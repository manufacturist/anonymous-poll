package page

import org.scalajs.dom.Element
import scalatags.JsDom.all.*

class NotFound() extends Page:
  override def render: Element =
    p("404 - Not found / implemented :(").render
