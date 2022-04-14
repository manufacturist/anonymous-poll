import component.Navbar
import org.scalajs.dom.Element
import scalatags.JsDom.all.*
import page.PageName

object PageRenderer {

  def apply(currentPage: PageName): Element =
    val navbar = new Navbar(currentPage)

    div(
      navbar.render
    ).render
}
