package component

import i18n.{I18N, I18NSupport}
import org.scalajs.dom.Element
import page.PageName
import scalatags.JsDom.all.*
import scalatags.JsDom.tags2.*

final class Navbar(currentPage: PageName) extends Component {

  private val navClass =
    `class` := "font-sans flex flex-col text-center sm:flex-row sm:text-left sm:justify-between py-4 px-6 bg-white shadow sm:items-baseline w-full"

  private val navDivAClass =
    `class` := "text-lg no-underline text-grey-darkest hover:text-blue-dark ml-4"

  private val linksByPageName = Map(
    PageName.Home    -> (a(href := "#Home", navDivAClass), I18NSupport.get(I18N.NavBar.HOME)),
    PageName.Create  -> (a(href := "#Create", navDivAClass), I18NSupport.get(I18N.NavBar.CREATE_POLL)),
    PageName.Answer  -> (a(href := "#Answer", navDivAClass), I18NSupport.get(I18N.NavBar.ANSWER_POLL)),
    PageName.Results -> (a(href := "#Results", navDivAClass), I18NSupport.get(I18N.NavBar.VIEW_RESULTS))
  )

  override def render: Element =
    nav(navClass)(
      div(`class` := "mb-2 text-2xl sm:mb-0")(
        a(href := "#Home")(I18NSupport.get(I18N.NavBar.TITLE))
      ),
      div(highlightCurrentPage)
    ).render

  private def highlightCurrentPage =
    for (page, (linkElement, text)) <- linksByPageName.toList
    yield
      if page == currentPage then linkElement(b(text))
      else linkElement(text)

}
