import akka.actor.Actor.Receive
import akka.actor._
import RedditDataModel._

import scala.concurrent.{Await, Future, ExecutionContext}
import scala.util.{Success, Failure}

/**
  * This actor processes messages containing information about 1 subreddit. It finds relevant information
  * about the subreddit's users, then analyzes that information to produce a list of new subreddits. The
  * results are sent back to MyUserActor
  */
object SubredditActor {

  def props = Props(new SubredditActor)

  case class SubredditMessage(subreddit: SubredditData, depth: Int) //message received from MyUserActor

  case class AnalyzedSubredditMessage(suggested: List[SubredditData], depth: Int) //message sent to MyUserActor

  case class FailedSubredditAnalysisMessage(reason: Throwable, depth: Int) //message sent to MyUserActor

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
      Future.sequence({
        for {
          recentComment ← recentComments
        } yield {
          for (similarComments ← redditService.getRecentCommentsBySameAuthor(recentComment, 2)) yield {
            println(s"got ${similarComments.size} similar comments")
            similarComments.map(c ⇒ SubredditData(c.postedSubreddit)).distinct
          }
        }
      }).onComplete {
        case Success(s) ⇒ senderActor ! AnalyzedSubredditMessage(s.flatten, depth)
        case Failure(e) ⇒
          //todo get something more specific, which message exactly failed
          senderActor ! FailedSubredditAnalysisMessage(e, depth)
      }
    }
  }
}
