package page

import component.Navbar
import org.scalajs.dom.Element
import page.PageName
import page.PageName.{Answer, Home, Results}
import scalatags.JsDom.all.*

object PageRenderer {

  def apply(currentPage: PageName): Element =
    val navbar = new Navbar(currentPage)

    val pageContent = currentPage match {
      case Home => new LandingPage()
      case _    => new NotFound()
    }

    div(
      navbar.render,
      pageContent.render
    ).render
}
