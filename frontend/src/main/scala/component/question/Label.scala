package component.question

import component.Component
import entity.Text as DomainText
import entity.dto.QuestionView
import org.scalajs.dom.*
import scalatags.JsDom.all.*
import scalatags.JsDom.tags2.*

class Label(text: DomainText) extends QuestionComponent:
  override def render: Element = label(
    `class` := "block uppercase tracking-wide text-gray-700 text-xs font-bold mb-2"
  )(text).render
