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
  implicit val timeout = Timeout(30, TimeUnit.SECONDS)
}


class RedditApiWrapper(clientId: String, clientSecret: String, redirectUri: String)(implicit val system: ActorSystem) {
  import RedditApiWrapper._
  import RedditDataModel._

  require(clientId != null, "missing client id")
  require(clientSecret != null, "missing client secret")
  require(redirectUri != null, "missing redirect uri")

  val BASE_URL = "https://www.reddit.com"
  val BASE_API_URL = s"$BASE_URL/api/v1"
  val OAUTH_BASE_URL = "https://oauth.reddit.com" //use this url as the base after the user is validated with oauth
  val CLIENT_ID = clientId
  val CLIENT_SECRET = clientSecret
  val REDIRECT_URI = redirectUri

  /**
    * First step in getting oauth to work. See: https://github.com/reddit/reddit/wiki/OAuth2
    *
    * @param scope
    * @return
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
    * Retreive the access token that lasts for 1 hr. Uses the code extracted from the authorization
    * redirect url query params.
    *
    * @param code the code
    */
  def retreiveAccessToken(code: String): HttpResponse = {
    val url = s"$BASE_API_URL/access_token"
    val form = FormData(Map("code" → code, "redirect_uri" → REDIRECT_URI, "grant_type" → "authorization_code"))

    val response = (IO(Http) ? (Post(Uri(url), form) ~> addCredentials(BasicHttpCredentials(CLIENT_ID, CLIENT_SECRET)))).mapTo[HttpResponse]
    Await.result(response, Duration.Inf)
  }

  /**
    * Call this endpoint when the token has expired, and a new one needs to be retrieved
    *
    * @param refreshToken the refresh token, from the response o
    * @return
    */
  def refreshAccessToken(refreshToken: String): Future[HttpResponse] = {
    val url = s"$BASE_API_URL/access_token"
    val form = FormData(Map("refresh_token" → refreshToken, "grant_type" → "refresh_token"))

    (IO(Http) ? (Post(Uri(url), form) ~> addCredentials(BasicHttpCredentials(CLIENT_ID, CLIENT_SECRET)))).mapTo[HttpResponse]
  }


  def getSubscribedSubreddits()(implicit token: OAuth2BearerToken): Future[HttpResponse] = {
    val url = s"$OAUTH_BASE_URL/subreddits/mine/subscriber.json?limit=100"
    (IO(Http) ? (Get(Uri(url)) ~> addCredentials(token))).mapTo[HttpResponse]
  }

  def getRecentCommentsForSubreddit(s: SubredditData): Future[HttpResponse] = {
    val url = s"$BASE_URL/r/${s.name}/comments.json?limit=100"
    (IO(Http) ? Get(Uri(url))).mapTo[HttpResponse]
  }
}
