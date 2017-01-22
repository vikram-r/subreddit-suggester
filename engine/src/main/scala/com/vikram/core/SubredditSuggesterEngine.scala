package com.vikram.core

import java.util.concurrent.TimeUnit

import SupervisorActor.{DoneMessage, StartMessage}
import RedditDataModel.SubredditData
import akka.actor.ActorSystem
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.stream.Materializer
import akka.util.Timeout
import akka.pattern.ask

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by vikram on 1/2/17.
  */
class SubredditSuggesterEngine(val redditService: RedditService)(implicit actorSystem: ActorSystem, ec: ExecutionContext, mat: Materializer) {

  def run(manualSubreddits: Set[String]): Future[String] = {
    //todo mistyped subreddits should throw an error, not be silently ignored
    _start(manualSubreddits.flatMap(s ⇒ redditService.validateSubreddit(s)))
  }

  protected def _start(subreddits: Set[SubredditData]): Future[String] = {
    implicit val timeout = Timeout(30, TimeUnit.MINUTES)

    println(s"Starting for Subreddits: ${subreddits.map(_.name).mkString(",")}")
    val supervisorActor = actorSystem.actorOf(SupervisorActor.props(redditService), "supervisorActor")
    val done = supervisorActor ? StartMessage(subreddits)

    for (result ← done) yield {
      result match {
        case DoneMessage(None, r) ⇒
          val result = prettyPrintString(r)
          prettyPrintResults(result)
          result
        case DoneMessage(Some(e), r) ⇒
          println(e) //terminated early
          ""
      }
      //todo is this correct? If the actorSystem passed in is the Play system, then I think Play handles the lifecycle
//        actorSystem.terminate()
    }
  }

  def prettyPrintString(results: Map[Int, Map[SubredditData, Int]]): String = {
    import CounterMapHelper._
    val lineSep = "\n-----------\n"
    println((for (depth ← results.keySet) yield {
        s"Depth: $depth$lineSep" + (
          for {
            subredditMap ← results.get(depth).toSeq
            (s, c) ← subredditMap.toList.sortBy(_._2).reverse
          } yield s"${s.name} - $c").mkString("\n")
      }).mkString(lineSep))

    results.aggregateCounterMaps.toList.sortBy(_._2).reverse.map(t ⇒ t._1.name + s" (${t._2})").mkString("\n")
  }

  def prettyPrintResults(resultString: String): Unit = {
    println("~~~~RESULTS~~~~")
    println("~~~~SUMMARY~~~~")
    println(resultString)
  }
}

/**
  * SubredditSuggesterEngine that must inject a RedditOauth2Service
  */
class OAuthSubredditSuggesterEngine(override val redditService: OAuthRedditService)(implicit actorSystem: ActorSystem, ec: ExecutionContext, mat: Materializer) extends SubredditSuggesterEngine(redditService) {

  def runWithOauthToken(token: String) = {
    implicit val oAuth2BearerToken = OAuth2BearerToken(token)
    _start(redditService.getSubscribedSubreddits().toSet)
  }
}
