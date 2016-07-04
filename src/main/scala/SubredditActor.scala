import akka.actor.Actor.Receive
import akka.actor.{ActorSystem, ActorLogging, Actor, Props}
import RedditDataModel._
import CommentActor._

import scala.concurrent.ExecutionContext

/**
  * This actor processes messages containing information about 1 subreddit. It finds relevant information
  * about the subreddit's users, then delegates user work to a CommentActor. It aggregates the analysis
  * of the users on this subreddit, and sends the results back to MyUserActor
  */
object SubredditActor {

  def props = Props(new SubredditActor)

  case class SubredditMessage(subreddit: SubredditData, depth: Int) //message received from MyUserActor

  case class AnalyzedSubredditMessage(suggested: List[SubredditData]) //message sent to MyUserActor

  val MAX_DEPTH = sys.props.get("depth").map(_.toInt).getOrElse(10)

}

class SubredditActor extends Actor with ActorLogging {
  import SubredditActor._

  import ExecutionContext.Implicits.global

  implicit val system: ActorSystem = context.system
  val redditService = new RedditService

  override def receive: Receive = {
    case SubredditMessage(s, d) ⇒ analyzeSubreddit(s, d)

  }


  def analyzeSubreddit(subredditData: SubredditData, depth: Int) = {
    for {
      r ← redditService.getRecentComments(subredditData)
      c ← r
    } {
      println(c.author)
      context.actorOf(CommentActor.props) ! CommentMessage(c, depth)
    }
  }
}
