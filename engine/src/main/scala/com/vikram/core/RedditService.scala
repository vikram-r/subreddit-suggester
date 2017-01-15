package com.vikram.core

import java.awt.Desktop
import java.net.URI

import akka.actor.ActorSystem
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.stream.Materializer

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.Try

class RedditService(val apiWrapper: RedditApiWrapper)(implicit val system: ActorSystem, ec: ExecutionContext, mat: Materializer) {

  import RedditDataModel._

  def oAuthRequestPermissions(): Unit = {
    val authResponse = apiWrapper.authorizeUser()
    for(redirectUrl ← authResponse.headers.find(_.name == "Location").map(_.value)){
      println(redirectUrl)
      if(Desktop.isDesktopSupported) {
        Desktop.getDesktop.browse(new URI(redirectUrl))
      }
      println("Pass the 'code' query parameter from the redirect url to this program by running `gradle run -Dcode=123` ")
    }
  }

  def oAuthGetToken(code: String): Option[String] = {
    Try(apiWrapper.retreiveAccessToken(code).access_token).toOption
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
