package page

import org.scalajs.dom.Element
import scalatags.JsDom.all.*

class LandingPage() extends Page:
  override def render: Element =
    p("Hello Scala.js!").render
