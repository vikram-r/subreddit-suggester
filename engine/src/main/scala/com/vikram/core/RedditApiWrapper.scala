package com.vikram.core

import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.stream.Materializer
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding._
import akka.http.scaladsl.model.headers.{BasicHttpCredentials, OAuth2BearerToken}
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.util.Timeout

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Await, Future}

object RedditApiWrapper {
  implicit val timeout = Timeout(5, TimeUnit.MINUTES)
}


class RedditApiWrapper(clientId: Option[String], clientSecret: Option[String], redirectUri: Option[String])(implicit val system: ActorSystem, mat: Materializer) {
  import RedditApiWrapper._
  import CustomJsonProtocols._
  import RedditDataModel._

  //todo this should probably be injected
  import ExecutionContext.Implicits.global

  val BASE_URL = "https://www.reddit.com"
  val BASE_API_URL = s"$BASE_URL/api/v1"
  val OAUTH_BASE_URL = "https://oauth.reddit.com" //use this url as the base after the user is validated with oauth
  lazy val CLIENT_ID = {
    require(clientId.isDefined, "missing client id")
    clientId.get
  }
  lazy val CLIENT_SECRET = {
    require(clientSecret.isDefined, "missing client secret")
    clientSecret.get
  }
  lazy val REDIRECT_URI = {
    require(redirectUri.isDefined, "missing redirect uri")
    redirectUri.get
  }

  val httpClient = Http(system)

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

    val response = httpClient.singleRequest(Get(Uri(url)))
    Await.result(response, Duration.Inf) //oauth login process needs to be synchronous
  }

  /**
    * Synchronous call to retreive the access token that lasts for 1 hr.
    * Uses the code extracted from the authorization redirect url query params.
    *
    * @param code the code
    * @return the response parsed as an OAuthTokenResponse
    */
  def retreiveAccessToken(code: String): OAuthTokenResponse = {
    val url = s"$BASE_API_URL/access_token"
    val form = FormData(Map("code" → code, "redirect_uri" → REDIRECT_URI, "grant_type" → "authorization_code"))
    val request = Post(Uri(url), form) ~> addCredentials(BasicHttpCredentials(CLIENT_ID, CLIENT_SECRET))
    val response = httpClient.singleRequest(request) flatMap {
      r ⇒ Unmarshal(r).to[OAuthTokenResponse]
    }

    Await.result(response, Duration.Inf)
  }

//  /**
//    * Asynchronous call to refresh an expired token
//    *
//    * @param refreshToken the refresh token
//    * @return future of the http response
//    */
//  def refreshAccessToken(refreshToken: String): Future[HttpResponse] = {
//    val url = s"$BASE_API_URL/access_token"
//    val form = FormData(Map("refresh_token" → refreshToken, "grant_type" → "refresh_token"))
//
//    httpClient.singleRequest(Post(Uri(url), form) ~> addCredentials(BasicHttpCredentials(CLIENT_ID, CLIENT_SECRET)))
//  }

  /**
    * Asynchronous call to get all subscribed subreddits for the logged in user (via oauth2)
    *
    * @param token the oauth2 bearer token
    * @return future of the response parsed as a RedditListingThing
    */
  def getSubscribedSubreddits()(implicit token: OAuth2BearerToken): Future[RedditListingThing] = {
    val url = s"$OAUTH_BASE_URL/subreddits/mine/subscriber.json?limit=100"
    httpClient.singleRequest(Get(Uri(url)) ~> addCredentials(token)) flatMap {
      r ⇒ Unmarshal(r).to[RedditListingThing]
    }
  }

  /**
    * Synchronous call to get more information about this subreddit
    *
    * @param s the subreddit to lookup
    * @return the response parsed as a RedditListingElement
    */
  def getSubredditInfo(s: SubredditData): RedditListingElement = {
    val url = s"$BASE_URL/r/${s.name}/about.json?limit=1"
    val response = httpClient.singleRequest(Get(Uri(url))) flatMap {
      r ⇒ Unmarshal(r).to[RedditListingElement]
    }
    Await.result(response, Duration.Inf)
  }

  //todo pagination
  /**
    * Asynchronous call to get recent comments for a subreddit
    *
    * @param s the subreddit to look up recent comments
    * @param limit the max number of comments to return
    * @return future of the response parsed as a RedditListingThing
    */
  def getRecentCommentsForSubreddit(s: SubredditData, limit: Int): Future[RedditListingThing] = {
    val url = s"$BASE_URL/r/${s.name}/comments.json?limit=$limit"
    httpClient.singleRequest(Get(Uri(url))) flatMap {
      r ⇒ Unmarshal(r).to[RedditListingThing]
    }
  }

  /**
    * Asynchronous call to get recent comments for a user
    *
    * @param u the user to look up recent comments
    * @param limit the max number of comments to return
    * @return future of the response parsed as a RedditListingThing
    */
  def getRecentCommentsForUser(u: String, limit: Int): Future[RedditListingThing] = {
    val url = s"$BASE_URL/user/$u.json?limit=$limit"
    httpClient.singleRequest(Get(Uri(url))) flatMap {
      r ⇒ Unmarshal(r).to[RedditListingThing]
    }
  }
}
