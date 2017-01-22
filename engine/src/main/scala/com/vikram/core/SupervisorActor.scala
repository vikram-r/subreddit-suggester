package com.vikram.core

import RedditDataModel.SubredditData
import SubredditActor.{AnalyzedSubredditMessage, SubredditMessage}
import akka.actor._

import scala.concurrent.ExecutionContext

/**
  * This actor is the "start" actor. It delegates work per subscribed subreddit to [[SubredditActor]].
  * It aggregates the results, then sends a complete message back to [[SubredditSuggesterEngine]]
  */
object SupervisorActor {

  //recommended way to create a Props for an Actor (http://doc.akka.io/docs/akka/snapshot/scala/actors.html)
  def props(redditService: RedditService)(implicit ec: ExecutionContext) = Props(new SupervisorActor(redditService))

  case class StartMessage(manualSubreddits: Set[SubredditData])

  case class DoneMessage(reasonNotCompleted: Option[String] = None,
                         suggestedSubreddits: Map[Int, Map[SubredditData, Int]] = Map.empty)

  val MAX_DEPTH = sys.props.get("depth").map(_.toInt).getOrElse(3)
}

//todo this entire class is a mess
class SupervisorActor(redditService: RedditService)(implicit ec: ExecutionContext) extends Actor with ActorLogging {
  import CounterMapHelper._
  import SupervisorActor._

  private var startActor: Option[ActorRef] = None //reference to start actor

  private var numMessagesAtDepth: Map[Int, Int] = Map.empty //number of messages to process per depth
  private var currDepth = 1
  private var numProcessed = 0 //number of subreddits processed at the current depth
  private var subscribedSubreddits: Set[SubredditData] = Set.empty

  private var subredditsProcessed: Map[Int, Map[SubredditData, Int]] = Map.empty //keeps track of subreddits discovered at each depth


  override def receive: Receive = {
    case StartMessage(manualSubreddits) ⇒
      startActor = Some(sender)
      subscribedSubreddits = manualSubreddits
      findSuggestedSubreddits(subscribedSubreddits, currDepth)

    case AnalyzedSubredditMessage(subreddits, depth) ⇒
      //todo hopefully in the future, I can figure out how to not require this condition
      require(depth == currDepth, s"currDepth = $currDepth is not equal to depth = $depth")
      println(s"numprocessed: $numProcessed, message depth: $depth")
      numProcessed += 1
      subredditsProcessed += (depth → subredditsProcessed.getOrElse(depth, Map.empty).updateCountersBy1(subreddits.filterNot(subscribedSubreddits.contains)))
      `continue?`()
    case _ ⇒
      log.error("Invalid Message")
  }

  private def `continue?`() = {
    //check if we're done processing at this depth
    println(s"numProcessed: $numProcessed vs numMessagesAtDepth: ${numMessagesAtDepth.getOrElse(currDepth, 0)}")
    if (numProcessed >= numMessagesAtDepth.getOrElse(currDepth, 0)) {
      if (currDepth < SupervisorActor.MAX_DEPTH) {
        println(s"done with depth $currDepth, which is < ${SupervisorActor.MAX_DEPTH}")
        numProcessed = 0
        currDepth += 1
        //todo 0 subreddits found at a depth is an edge case, which will either cause a NSEE here, or prevent program from terminating
        findSuggestedSubreddits(subredditsProcessed.get(currDepth - 1).get.keySet, currDepth)
      } else {
        //done
        startActor.foreach(_ ! DoneMessage(suggestedSubreddits = subredditsProcessed))
      }
    }
  }

  def findSuggestedSubreddits(subreddits: Set[SubredditData], depth: Int): Unit = {
    println(s"Starting for Depth: $depth, with ${subreddits.size} messages!")
    numMessagesAtDepth += depth → subreddits.size //record # messages sent
    //send a message per subreddit to SubredditActor
    for (s ← subreddits) context.actorOf(SubredditActor.props(redditService)) ! SubredditMessage(s, depth)
  }

  def validateSubreddit(name: String): Option[SubredditData] = {
    redditService.validateSubreddit(name)
  }

}
