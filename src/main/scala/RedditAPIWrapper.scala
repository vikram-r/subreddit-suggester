import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.io.IO
import akka.util.Timeout
import spray.can.Http
import spray.http.HttpMethods._
import spray.http._
import spray.httpx.RequestBuilding._
//import spray.client.pipelining._ todo maybe use this instead of RequestBuilding (seems more powerful)

import scala.concurrent.{Await, Future}

object RedditApiWrapper {
  implicit val system = ActorSystem()
  implicit val timeout = Timeout(30, TimeUnit.SECONDS)
}


class RedditApiWrapper() {
  import RedditApiWrapper._

  //todo load from config
  val BASE_URL = "https://www.reddit.com/api/v1"
  val OAUTH_BASE_URL = "https://oauth.reddit.com/api/v1" //use this url as the base after the user is validated with oauth
  val CLIENT_ID = "G7_Lv9YNO8oIDA"
  val CLIENT_SECRET: String = null
  val REDIRECT_URI = "https://127.0.0.1:65010/authorize_callback"


  /**
    * First step in getting oauth to work. See: https://github.com/reddit/reddit/wiki/OAuth2
    *
    * Spray will automatically perform the redirect, due to max-redirects = 1 in application.conf
    * @param scope
    * @return
    */
  def authorizeUser(scope: List[String] = List("mysubreddits","history")): Future[HttpResponse] = {
    val authState = UUID.randomUUID()
    val duration = "permanent"
    val url = s"$BASE_URL/authorize?client_id=$CLIENT_ID&response_type=code&state=$authState&" +
      s"redirect_uri=$REDIRECT_URI&duration=$duration&scope=${scope.mkString(",")}"

    (IO(Http) ? Get(Uri(url))).mapTo[HttpResponse]
  }

  /**
    * Retreive the refresh token that lasts for 1 hr. Uses the code extracted from the authorization
    * redirect url query params.
    *
    * @param code
    */
  def retreiveAccessToken(code: String): Future[HttpResponse] = {
    val url = s"$BASE_URL/access_token"
    val form = FormData(Map("code" → code, "redirect_uri" → REDIRECT_URI, "grant_type" → "authorization_code"))

    (IO(Http) ? (Post(Uri(url), form) ~> addCredentials(BasicHttpCredentials(CLIENT_ID, CLIENT_SECRET)))).mapTo[HttpResponse]
  }

  /**
    * Call this endpoint when the token has expired, and a new one needs to be retreived
    *
    * @param refreshToken the refresh token, from the response o
    * @return
    */
  def refreshAccessToken(refreshToken: String): Future[HttpResponse] = {
    val url = s"$BASE_URL/access_token"
    val form = FormData(Map("refresh_token" → refreshToken, "grant_type" → "refresh_token"))

    (IO(Http) ? (Post(Uri(url), form) ~> addCredentials(BasicHttpCredentials(CLIENT_ID, CLIENT_SECRET)))).mapTo[HttpResponse]
  }


  def getSubscribedSubreddits()(implicit token: OAuth2BearerToken): Future[HttpResponse] = {
    val url = s"$BASE_URL/subreddits/mine/subscriber"
    (IO(Http) ? (Get(Uri(url)) ~> addCredentials(token))).mapTo[HttpResponse]
  }
}