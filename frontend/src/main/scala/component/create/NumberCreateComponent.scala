package component.create

import component.Component
import org.scalajs.dom.Element
import scalatags.JsDom.all.*

class NumberCreateComponent extends Component {

  override def render: Element =
    p("Number question").render
}
