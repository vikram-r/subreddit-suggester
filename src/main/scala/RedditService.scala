import java.awt.Desktop
import java.net.URI
import akka.actor.ActorSystem
import akka.util.Timeout
import spray.http.OAuth2BearerToken
import spray.json._
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}
import scala.util.Try

class RedditService(implicit val system: ActorSystem) {

  import RedditDataModel._
  import CustomJsonProtocols._
  import ExecutionContext.Implicits.global

  val apiWrapper = new RedditApiWrapper(
    clientId = sys.props.get("com.vikram.subredditsuggester.client_id").get,
    clientSecret = sys.props.get("com.vikram.subredditsuggester.client_secret").get,
    redirectUri = sys.props.get("com.vikram.subredditsuggester.redirect_uri").get
  )

  def oAuthRequestPermissions(): Unit = {
    val authResponse = apiWrapper.authorizeUser()
    for(redirectUrl ← authResponse.headers.find(_.name == "Location").map(_.value)){
      println(redirectUrl)
      if(Desktop.isDesktopSupported) {
        Desktop.getDesktop.browse(new URI(redirectUrl))
      }
      println("Pass the 'code' query parameter from the redirect url to this program by running `gradle run -Dcode=123` ")
    }
  }

  def oAuthGetToken(code: String): Option[String] = {
    val tokenResponse = apiWrapper.retreiveAccessToken(code)
    Try(tokenResponse.entity.asString.parseJson.convertTo[OAuthTokenResponse].access_token).toOption
  }

  def getSubscribedSubreddits()(implicit token: OAuth2BearerToken): List[String] = {
    val response = Await.result(apiWrapper.getSubscribedSubreddits, Duration.Inf)

    response.entity.asString.parseJson.convertTo[RedditListingThing].data.children.map(_.toSubredditData.name)
  }
}
