package page

import component.containerDiv
import org.scalajs.dom.Element
import scalatags.JsDom.all.*

class NotFoundPage() extends Page:
  override def renderElement: Element =
    containerDiv(
      p("404 - Not found / implemented :(")
    ).render.render
