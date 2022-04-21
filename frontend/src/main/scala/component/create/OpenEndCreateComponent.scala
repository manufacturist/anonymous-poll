package component.create

import component.Component
import org.scalajs.dom.Element
import scalatags.JsDom.all.*

class OpenEndCreateComponent extends Component {

  override def render: Element =
    p("OpenEnd question").render
}
