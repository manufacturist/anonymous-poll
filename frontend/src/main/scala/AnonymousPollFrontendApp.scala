import component.*
import org.scalajs.dom
import org.scalajs.dom.*
import page.*

import scala.util.{Failure, Success, Try}

object AnonymousPollFrontendApp {

  private val appId           = "anonymous-poll-app"
  private lazy val appElement = document.getElementById(appId)

  def main(args: Array[String]): Unit = {
    renderPageForHash(None)

    window.onhashchange = event => {
      val url          = event.newURL
      val hashPosition = url.lastIndexOf("#") + 1
      val hash         = url.substring(hashPosition)

      renderPageForHash(Some(hash))
    }
  }

  private def renderPageForHash(hash: Option[String]): Unit =
    val value = hash match {
      case Some(value) => value
      case None        => window.location.hash.substring(1)
    }

    val pageName = Try(PageName.valueOf(value)) match {
      case Failure(exception) => PageName.Home
      case Success(value)     => value
    }

    appElement.innerHTML = PageRenderer(pageName).innerHTML
}
