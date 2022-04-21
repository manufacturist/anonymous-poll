package component.create

import component.Component
import org.scalajs.dom.Element
import scalatags.JsDom.all.*

class ChoiceCreateComponent extends Component {

  override def render: Element =
    p("Choice question").render
}
