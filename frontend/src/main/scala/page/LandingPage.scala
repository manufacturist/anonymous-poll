package page

import component.*
import org.scalajs.dom.Element
import scalatags.JsDom.all.*

class LandingPage() extends Page:
  override def render: Element =
    container(
      p("This is an anonymous poll app, which implies the following:"),
      ul(`class` := "list-disc px-6")(
        li("You can only vote once using email received link"),
        li("The poll is deleted one week after its creation OR on server restart")
      )
    ).render
