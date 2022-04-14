import cats.effect.*
import cats.effect.unsafe.IORuntime
import client.PollApiClient
import component.*
import org.http4s.Uri
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import org.scalajs.dom.*
import page.PageRenderer

object AnonymousPollFrontendApp extends ResourceApp.Simple {

  given IORuntime = cats.effect.unsafe.implicits.global

  override def run: Resource[IO, Unit] =
    for given Client[IO] <- EmberClientBuilder.default[IO].build
    yield {
      val appId      = "anonymous-poll-app"
      val appElement = document.getElementById(appId)

      val pollApiClient = new PollApiClient(Uri.unsafeFromString("http://127.0.0.1:1337"))
      val pageRenderer  = new PageRenderer(appElement, pollApiClient)

      // Initial rendering
      pageRenderer.render(None)

      // SPA-like event handler
      window.onhashchange = pageRenderer.render(_: HashChangeEvent)
    }
}
