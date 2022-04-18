package component

import entity.Text
import scalatags.JsDom.all.*

package object question:
  def labelElement(text: Text) =
    tag("label")(
      `class` := "block uppercase tracking-wide text-gray-700 text-xs font-bold mb-2"
    )(text).render
