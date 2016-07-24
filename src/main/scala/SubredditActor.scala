import akka.actor.Actor.Receive
import akka.actor._
import RedditDataModel._
import CommentActor._

import scala.concurrent.{Await, Future, ExecutionContext}

/**
  * This actor processes messages containing information about 1 subreddit. It finds relevant information
  * about the subreddit's users, then delegates user work to a CommentActor. It aggregates the analysis
  * of the users on this subreddit, and sends the results back to MyUserActor
  */
object SubredditActor {

  def props = Props(new SubredditActor)

  case class SubredditMessage(subreddit: SubredditData, depth: Int) //message received from MyUserActor

  case class AnalyzedSubredditMessage(suggested: List[SubredditData], depth: Int) //message sent to MyUserActor

  val MAX_DEPTH = sys.props.get("depth").map(_.toInt).getOrElse(10)
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
    println(s"analyzing depth: $depth")
    val senderActor = sender

    //todo this is going very slow, and eventually stopping
    for (recentComments ← redditService.getRecentCommentsForSubreddit(subredditData, 10)) {
      Future.sequence({
        for {
          recentComment ← recentComments
        } yield {
          for (similarComments ← redditService.getRecentCommentsBySameAuthor(recentComment, 10)) yield {
            similarComments.map(c ⇒ SubredditData(c.postedSubreddit)).distinct
          }
        }
      }).foreach(s ⇒ senderActor ! AnalyzedSubredditMessage(s.flatten, depth))
    }
  }
}
