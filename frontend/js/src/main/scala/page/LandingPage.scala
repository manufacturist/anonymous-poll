package page

import component.*
import i18n.*
import org.scalajs.dom.Element
import scalatags.JsDom.all.*

class LandingPage() extends Page:
  override def renderElement: Element =
    containerDiv(
      p(I18NSupport.get(I18N.Home.DEFINITION)),
      ul(`class` := "list-disc px-6")(
        li(I18NSupport.get(I18N.Home.IDEA_1)),
        li(I18NSupport.get(I18N.Home.IDEA_2))
      )
    ).render
