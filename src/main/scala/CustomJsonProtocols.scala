import spray.json._
import RedditDataModel._

trait CustomJsonProtocols extends DefaultJsonProtocol {
  implicit val oAuthTokenResponseFormat = jsonFormat5(OAuthTokenResponse)

  implicit val redditListingElementFormat = jsonFormat2(RedditListingElement)

  implicit val redditListingFormat = jsonFormat4(RedditListing)

  implicit val redditListingThingFormat = jsonFormat2(RedditListingThing)

}

object CustomJsonProtocols extends CustomJsonProtocols {}