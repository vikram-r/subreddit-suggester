import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.actor.Status.Success
import akka.pattern.ask
import akka.io.IO
import akka.util.Timeout
import com.sun.net.httpserver.Authenticator.Failure
import spray.can.Http
import spray.http.HttpMethods._
import spray.http._
import spray.httpx.RequestBuilding._
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object RedditApiWrapper {
  implicit val timeout = Timeout(5, TimeUnit.MINUTES)
}


class RedditApiWrapper(clientId: String, clientSecret: String, redirectUri: String)(implicit val system: ActorSystem) {
  import RedditApiWrapper._
  import RedditDataModel._

  val BASE_URL = "https://www.reddit.com"
  val BASE_API_URL = s"$BASE_URL/api/v1"
  val OAUTH_BASE_URL = "https://oauth.reddit.com" //use this url as the base after the user is validated with oauth
  lazy val CLIENT_ID = {
    require(clientId != null, "missing client id")
    clientId
  }
  lazy val CLIENT_SECRET = {
    require(clientSecret != null, "missing client secret")
    clientSecret
  }
  lazy val REDIRECT_URI = {
    require(redirectUri != null, "missing redirect uri")
    redirectUri
  }

  /**
    * Synchronous call to request oauth2 authorization.
    * See: https://github.com/reddit/reddit/wiki/OAuth2
    *
    * @param scope the list of scope permissions requested
    * @return the http response
    */
  def authorizeUser(scope: List[String] = List("mysubreddits","history")): HttpResponse = {
    val authState = UUID.randomUUID()
    val duration = "permanent"
    val url = s"$BASE_API_URL/authorize?client_id=$CLIENT_ID&response_type=code&state=$authState&" +
      s"redirect_uri=$REDIRECT_URI&duration=$duration&scope=${scope.mkString(",")}"

    val response = (IO(Http) ? Get(Uri(url))).mapTo[HttpResponse]
    Await.result(response, Duration.Inf) //oauth login process needs to be synchronous
  }

  /**
    * Synchronous call to retreive the access token that lasts for 1 hr.
    * Uses the code extracted from the authorization redirect url query params.
    *
    * @param code the code
    * @return the http response
    */
  def retreiveAccessToken(code: String): HttpResponse = {
    val url = s"$BASE_API_URL/access_token"
    val form = FormData(Map("code" → code, "redirect_uri" → REDIRECT_URI, "grant_type" → "authorization_code"))

    val response = (IO(Http) ? (Post(Uri(url), form) ~> addCredentials(BasicHttpCredentials(CLIENT_ID, CLIENT_SECRET)))).mapTo[HttpResponse]
    Await.result(response, Duration.Inf)
  }

  /**
    * Asynchronous call to refresh an expired token
    *
    * @param refreshToken the refresh token
    * @return future of the http response
    */
  def refreshAccessToken(refreshToken: String): Future[HttpResponse] = {
    val url = s"$BASE_API_URL/access_token"
    val form = FormData(Map("refresh_token" → refreshToken, "grant_type" → "refresh_token"))

    (IO(Http) ? (Post(Uri(url), form) ~> addCredentials(BasicHttpCredentials(CLIENT_ID, CLIENT_SECRET)))).mapTo[HttpResponse]
  }

  /**
    * Asynchronous call to get all subscribed subreddits for the logged in user (via oauth2)
    *
    * @param token the oauth2 bearer token
    * @return future of the http response
    */
  def getSubscribedSubreddits()(implicit token: OAuth2BearerToken): Future[HttpResponse] = {
    val url = s"$OAUTH_BASE_URL/subreddits/mine/subscriber.json?limit=100"
    (IO(Http) ? (Get(Uri(url)) ~> addCredentials(token))).mapTo[HttpResponse]
  }

  /**
    * Synchronous call to get more information about this subreddit
    *
    * @param s the subreddit to lookup
    * @return future of the http response
    */
  def getSubredditInfo(s: SubredditData): HttpResponse = {
    val url = s"$BASE_URL/r/${s.name}/about.json?limit=1"
    val response = (IO(Http) ? Get(Uri(url))).mapTo[HttpResponse]
    Await.result(response, Duration.Inf)
  }

  //todo pagination
  /**
    * Asynchronous call to get recent comments for a subreddit
    *
    * @param s the subreddit to look up recent comments
    * @param limit the max number of comments to return
    * @return future of the http response
    */
  def getRecentCommentsForSubreddit(s: SubredditData, limit: Int): Future[HttpResponse] = {
    val url = s"$BASE_URL/r/${s.name}/comments.json?limit=$limit"
    (IO(Http) ? Get(Uri(url))).mapTo[HttpResponse]
  }

  /**
    * Asynchronous call to get recent comments for a user
    *
    * @param u the user to look up recent comments
    * @param limit the max number of comments to return
    * @return future of the http response
    */
  def getRecentCommentsForUser(u: String, limit: Int): Future[HttpResponse] = {
    val url = s"$BASE_URL/user/$u.json?limit=$limit"
    (IO(Http) ? Get(Uri(url))).mapTo[HttpResponse]
  }
}
