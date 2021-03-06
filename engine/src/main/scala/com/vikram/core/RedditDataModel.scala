package com.vikram.core

import spray.json.JsValue

object RedditDataModel {
  import CustomJsonProtocols._

  case class OAuthTokenResponse(access_token: String, token_type: String, expires_in: Long, refresh_token: String, scope: String)

  case class RedditListingThing(kind: String, data: RedditListing)

  case class RedditListing(before: Option[String], after: Option[String], modhash: Option[String], children: List[RedditListingElement])

  /**
    * Generic data for listing api responses for easier json processing. This pattern makes the json
    * processing easier (for now), since most of the response is not needed
    *
    * @param kind the kind of listing element
    * @param data the actual data stored in a map. The values must be converted to objects before use.
    */
  class RedditListingElement(val kind: String, val data: Map[String, JsValue]) {
    lazy val dataAsSubredditData = {
      require(kind == "t5")
      SubredditData(name = data.get("display_name").map(_.convertTo[String]).get)
    }

    lazy val dataAsCommentData = {
      //todo there should be a different representation for kind = "t3", which are selfposts/link submissions
      require(kind == "t1")
      CommentData(
        author = data.get("author").map(_.convertTo[String]).get,
        comment = data.get("body").map(_.convertTo[String]).get,
        postedSubreddit = data.get("subreddit").map(_.convertTo[String]).get
      )
    }
  }

  case class SubredditData(name: String)

  case class CommentData(author: String, comment: String, postedSubreddit: String)

}
