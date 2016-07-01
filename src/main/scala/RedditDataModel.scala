import spray.json.{JsString, JsValue}

object RedditDataModel {
  import CustomJsonProtocols._

  case class OAuthTokenResponse(access_token: String, token_type: String, expires_in: Long, refresh_token: String, scope: String)

  case class RedditListingThing(kind: String, data: RedditListing)

  case class RedditListing(before: Option[String], after: Option[String], modhash: Option[String], children: List[RedditListingElement])

  //Generic data for listing api responses for easier json processing. This pattern makes the json
  // processing easier (for now), since most of the response is not needed
  case class RedditListingElement(kind: String, data: Map[String, JsValue]) {

    //These defs should be used to extract data from the data map into an expected type
    def toSubredditData: SubredditData = {
      require(kind == "t5")
      SubredditData(data.get("display_name").map(_.convertTo[String]).get)
    }
  }

  case class SubredditData(name: String)




}
