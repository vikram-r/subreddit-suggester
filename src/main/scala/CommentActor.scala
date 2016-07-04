import akka.actor.Actor.Receive
import akka.actor.{ActorLogging, Actor, Props}
import RedditDataModel._
import SubredditActor._


/**
  * This actor processes messages containing information about 1 comment. It looks up information about the
  * author of the comment to find a list of suggested subreddits (based on the author). The results are sent
  * back to SubredditActor, with the depth incremented.
  */
object CommentActor {
  def props = Props(new CommentActor)

  case class CommentMessage(comment: CommentData, depth: Int) //message sent from SubredditActor

  case class AnalyzedCommentMessage(subscribed: List[SubredditData], depth: Int) //message sent back to SubredditActor
}

class CommentActor extends Actor with ActorLogging {
  import CommentActor._

  override def receive: Receive = {
    case CommentMessage(u, d) ⇒ analyzeComment(u, d)

  }

  def analyzeComment(commentData: CommentData, depth: Int) = {
    val author = commentData.author
    //todo look up authors recent comments, then send message back to SubredditActor with depth incremented
  }
}
