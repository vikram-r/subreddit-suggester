package com.vikram.core

import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.stream.Materializer

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.Try

class RedditService(val apiWrapper: RedditApiWrapper)(implicit system: ActorSystem, ec: ExecutionContext, mat: Materializer) {

  import RedditDataModel._

  def validateSubreddit(name: String): Option[SubredditData] = {
    Try {
      val response = apiWrapper.requestSubredditInfo(SubredditData(name))
      response.dataAsSubredditData
    }.toOption
  }

  //todo comments are filtering out "t3" kinds, which are selfposts and links
  def getRecentCommentsForSubreddit(subredditData: SubredditData, limit: Int): Future[List[CommentData]] = {
    val response = apiWrapper.requestRecentCommentsForSubreddit(subredditData, limit)
    response.map(_.data.children.filter(_.kind == "t1").map(_.dataAsCommentData))
  }

  //todo comments are filtering out "t3" kinds, which are selfposts and links
  def getRecentCommentsBySameAuthor(commentData: CommentData, limit: Int): Future[List[CommentData]] = {
    val response = apiWrapper.requestRecentCommentsForUser(commentData.author, limit)
    response.map(_.data.children.filter(_.kind == "t1").map(_.dataAsCommentData))
  }
}

/**
  * A [[RedditApiWrapper]] with extended OAuth2 functionality*
  *
  */
class OAuthRedditService(override val apiWrapper: RedditOauth2ApiWrapper)(implicit system: ActorSystem, ec: ExecutionContext, mat: Materializer) extends RedditService(apiWrapper) {

  import RedditDataModel._

  def getSubscribedSubreddits()(implicit token: OAuth2BearerToken): List[SubredditData] = {
    val response = Await.result(apiWrapper.requestSubscribedSubreddits, Duration.Inf)
    //todo failure case
    response.data.children.map(_.dataAsSubredditData)
  }

  def oAuthUrl(authState: UUID, scope: List[String] = List("mysubreddits","history"), duration: String = "permanent"): String = {
    apiWrapper.requestAuthorizationUrl(authState, scope, duration)
  }

  def oAuthGetToken(code: String): Future[String] = {
    apiWrapper.requestAccessToken(code).map(_.access_token)
  }

}
