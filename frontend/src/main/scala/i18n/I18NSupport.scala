package i18n

import config.DocumentLanguage
import org.scalajs.dom.document

object I18NSupport {
  private lazy val language = DocumentLanguage.valueOf(document.documentElement.getAttribute("lang"))

  def get(i18nId: String): String =
    languageMaps(language)(i18nId)
}
