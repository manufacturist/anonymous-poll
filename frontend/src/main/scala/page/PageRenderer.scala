package page

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import client.PollApiClient
import component.{Footer, Navbar}
import org.http4s.Uri
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import org.scalajs.dom.{Element, HashChangeEvent, window}
import page.PageName
import page.PageName.*
import scalatags.JsDom.all.*

import scala.util.{Failure, Success, Try}

class PageRenderer(appElement: Element, pollApiClient: PollApiClient)(using Client[IO], IORuntime) {

  def render(event: HashChangeEvent): Unit =
    val url          = event.newURL
    val hashPosition = url.lastIndexOf("#") + 1
    val hash         = url.substring(hashPosition)

    render(Some(hash))

  def render(hash: Option[String]): Unit =
    val value = hash match {
      case Some(value) => value
      case None        => window.location.hash.substring(1)
    }

    val pageName = Try(PageName.valueOf(value)) match {
      case Failure(exception) => PageName.Home
      case Success(value)     => value
    }

    val navbar = new Navbar(pageName)

    val pageContent = pageName match {
      case Home   => new LandingPage()
      case Answer => new AnswerPollPage(pollApiClient)
      case _      => new NotFound()
    }

    val renderedPage = div(
      navbar.render,
      pageContent.render,
      Footer.render
    ).render

    appElement.innerHTML = renderedPage.innerHTML
}