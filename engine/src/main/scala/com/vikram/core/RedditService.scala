package com.vikram.core

import java.awt.Desktop
import java.net.URI
import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.stream.Materializer

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.Try

class RedditService(val apiWrapper: RedditApiWrapper)(implicit val system: ActorSystem, ec: ExecutionContext, mat: Materializer) {

  import RedditDataModel._

  def oAuthUrl(authState: UUID, scope: List[String] = List("mysubreddits","history"), duration: String = "permanent"): String = {
    apiWrapper.authorizationUrl(authState, scope, duration)
  }

  def oAuthGetToken(code: String): Future[String] = {
    apiWrapper.retreiveAccessToken(code).map(_.access_token)
  }

  def validateSubreddit(name: String): Option[SubredditData] = {
    Try {
      val response = apiWrapper.getSubredditInfo(SubredditData(name))
      response.dataAsSubredditData
    }.toOption
  }

  def getSubscribedSubreddits()(implicit token: OAuth2BearerToken): List[SubredditData] = {
    val response = Await.result(apiWrapper.getSubscribedSubreddits, Duration.Inf)
    //todo failure case
    response.data.children.map(_.dataAsSubredditData)
  }

  //todo comments are filtering out "t3" kinds, which are selfposts and links
  def getRecentCommentsForSubreddit(subredditData: SubredditData, limit: Int): Future[List[CommentData]] = {
    val response = apiWrapper.getRecentCommentsForSubreddit(subredditData, limit)
    response.map(_.data.children.filter(_.kind == "t1").map(_.dataAsCommentData))
  }

  //todo comments are filtering out "t3" kinds, which are selfposts and links
  def getRecentCommentsBySameAuthor(commentData: CommentData, limit: Int): Future[List[CommentData]] = {
    val response = apiWrapper.getRecentCommentsForUser(commentData.author, limit)
    response.map(_.data.children.filter(_.kind == "t1").map(_.dataAsCommentData))
  }

}
