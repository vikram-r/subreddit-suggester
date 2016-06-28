import spray.json.DefaultJsonProtocol
import RedditService._

trait CustomJsonProtocols extends DefaultJsonProtocol {
  implicit val oAuthTokenResponseFormat = jsonFormat5(OAuthTokenResponse)
}

object CustomJsonProtocols extends CustomJsonProtocols {}