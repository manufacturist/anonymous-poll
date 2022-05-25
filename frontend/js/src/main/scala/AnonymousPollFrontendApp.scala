import cats.effect.*
import client.PollApiClient
import page.PageRenderer
import org.http4s.Uri
import org.http4s.client.Client
import org.http4s.dom.FetchClientBuilder
import org.scalajs.dom.*

object AnonymousPollFrontendApp extends ResourceApp.Simple {

  given Client[IO] = FetchClientBuilder[IO].create

  override def run: Resource[IO, Unit] = Resource.eval {
    val appId      = "anonymous-poll-app"
    val appElement = document.getElementById(appId)

    val pollApiClient = new PollApiClient(Uri.unsafeFromString("http://192.168.0.24:1337"))
    val pageRenderer  = new PageRenderer(appElement, pollApiClient)

    // Initial rendering
    pageRenderer.render(None) *> IO.delay {
      // Old school AngularJs SPA-like event handler
      window.onhashchange = pageRenderer.render(_: HashChangeEvent).syncStep.unsafeRunSync()
    }
  }
}
