package component

import org.scalajs.dom.Element
import page.PageName
import scalatags.JsDom.all.*
import scalatags.JsDom.tags2.*

object Footer extends Component {
  val renderedElement: Element =
    footer(
      div(`class` := "container mx-auto px-6")(
        div(`class` := "mt-16 border-t-2 border-gray-300 flex flex-col items-center")(
          div(`class` := "sm:w-2/3 text-center py-6")(
            p(`class` := "text-sm text-blue-800 font-bold mb-2")(
              a(
                href    := "https://github.com/manufacturist/anonymous-poll",
                `class` := "hover:text-blue-200",
                target  := "_blank"
              )("\uD83D\uDC49\u00A0\u00A0Code on GitHub")
            )
          )
        )
      )
    ).render

  override def render: Element =
    renderedElement

}
