package page

import component.container
import org.scalajs.dom.Element
import scalatags.JsDom.all.*

class CreatePollPage() extends Page:
  override def render: Element =
    container(
      p("404 - Not found / implemented :(")
    ).render.render
