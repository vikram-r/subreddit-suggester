package com.vikram.core

import RedditDataModel._
import akka.actor._
import akka.pattern.pipe

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

/**
  * This actor processes messages containing information about 1 subreddit. It finds relevant information
  * about the subreddit's users, then analyzes that information to produce a list of new subreddits. The
  * results are sent back to GARBAGE.MyUserActor
  */
object SubredditActor {

  def props = Props(new SubredditActor)

  case class SubredditMessage(subreddit: SubredditData, depth: Int) //message received from GARBAGE.MyUserActor

  case class AnalyzedSubredditMessage(suggested: List[SubredditData], depth: Int) //message sent to GARBAGE.MyUserActor
}

class SubredditActor extends Actor with ActorLogging {
  import SubredditActor._

  import ExecutionContext.Implicits.global

  implicit val system: ActorSystem = context.system
  val redditService = new RedditService

  override def receive: Receive = {
    case SubredditMessage(s, d) ⇒
      analyzeSubreddit(s, d)
  }

  def analyzeSubreddit(subredditData: SubredditData, depth: Int) = {
    val senderActor = sender
    for (recentComments ← redditService.getRecentCommentsForSubreddit(subredditData, 2)) {
      println(s"got ${recentComments.size} recent comments for sub")
      val futures = for {
        recentComment ← recentComments
      } yield {
        for (similarComments ← redditService.getRecentCommentsBySameAuthor(recentComment, 2)) yield {
          similarComments.map(c ⇒ SubredditData(c.postedSubreddit)).distinct
        }
      }

      val result = Future.fold(futures.map(wrapFutureWithTry))(List.empty[SubredditData]) {
        case (accum, cs) ⇒
          accum ++ {
            cs match {
              case Success(s) ⇒ s
              case Failure(e) ⇒
                println(s"Failed getting subreddits from a comment, reason: ${e.getMessage}")
                List.empty[SubredditData]
            }
          }
      }.map(ls ⇒ AnalyzedSubredditMessage(ls.distinct, depth))

      result.pipeTo(senderActor) // pipe the result back
    }
  }

  // way to ensure entire subreddit message doesn't fail if 1 comment request throws an exception
  private def wrapFutureWithTry[T](future: Future[T]): Future[Try[T]] = future.map(Success(_)).recover {
    case e ⇒ Failure(e)
  }
}
