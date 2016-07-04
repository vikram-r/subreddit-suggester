import spray.json.{JsString, JsValue}

object RedditDataModel {
  import CustomJsonProtocols._

  case class OAuthTokenResponse(access_token: String, token_type: String, expires_in: Long, refresh_token: String, scope: String)

  case class RedditListingThing(kind: String, data: RedditListing)

  case class RedditListing(before: Option[String], after: Option[String], modhash: Option[String], children: List[RedditListingElement])

  /**
    * Generic data for listing api responses for easier json processing. This pattern makes the json
    * processing easier (for now), since most of the response is not needed
    * @param kind the kind of listing element
    * @param data the actual data stored in a map. The values must be converted to objects before use.
    */
  class RedditListingElement(val kind: String, val data: Map[String, JsValue]) {
    lazy val dataAsSubredditData = {
      require(kind == "t5")
      SubredditData(data)
    }

    lazy val dataAsCommentData = {
      require(kind == "t1")
      CommentData(data)
    }
  }

  case class SubredditData(data: Map[String, JsValue]) {
    lazy val name = data.get("display_name").map(_.convertTo[String]).get
  }

  case class CommentData(data: Map[String, JsValue]) {
    lazy val author = data.get("author").map(_.convertTo[String]).get
    lazy val comment = data.get("body").map(_.convertTo[String]).get
  }

}
