package com.vikram.core

import RedditDataModel.SubredditData
import SubredditActor.{AnalyzedSubredditMessage, SubredditMessage}
import akka.actor._
import spray.http.OAuth2BearerToken

/**
  * This actor is the "start" actor. It finds the subreddits the user is currently subscribed to, then
  * kicks off messages per subscribed subreddit to other actors. It aggregates the results, then sends
  * a complete message back to Main
  */
object MyUserActor {

  //recommended way to create a Props for an Actor (http://doc.akka.io/docs/akka/snapshot/scala/actors.html)
  def props = Props(new MyUserActor)

  case class StartMessage(token: Option[String],
                          code: Option[String],
                          manualSubreddits: Option[Set[String]])

  case class DoneMessage(reasonNotCompleted: Option[String] = None,
                         suggestedSubreddits: Map[Int, Map[SubredditData, Int]] = Map.empty)

  val MAX_DEPTH = sys.props.get("depth").map(_.toInt).getOrElse(3)
}

class MyUserActor extends Actor with ActorLogging {
  import CounterMapHelper._
  import MyUserActor._

  //todo can GARBAGE.RedditService and/or GARBAGE.RedditApiWrapper be singletons?
  //the actorsystem and timeout gets implicitly passed to the GARBAGE.RedditService and GARBAGE.RedditApiWrapper
  implicit val system: ActorSystem = context.system

  val redditService = new RedditService

  private var startActor: Option[ActorRef] = None //reference to start actor

  private var numMessagesAtDepth: Map[Int, Int] = Map.empty //number of messages to process per depth
  private var currDepth = 1
  private var numProcessed = 0 //number of subreddits processed at the current depth
  private var subscribedSubreddits: Set[SubredditData] = Set.empty

  private var subredditsProcessed: Map[Int, Map[SubredditData, Int]] = Map.empty //keeps track of subreddits discovered at each depth


  override def receive: Receive = {
    case StartMessage(token, code, manualSubreddits) ⇒
      startActor = Some(sender)

      /*
      If manual subreddits are set, don't login. If token is set use token. If code is set, retrieve token then use it. If none
      are set, open webpage asking user to login.
      */
      manualSubreddits.map(_.flatMap(s ⇒ validateSubreddit(s))).orElse(
        token.orElse(
          code match {
            case Some(c) ⇒
              redditService.oAuthGetToken(c)
            case None ⇒
              redditService.oAuthRequestPermissions()
              None
          }).map {
          oa ⇒
            implicit val oAuth2BearerToken = OAuth2BearerToken(oa)
            findMySubscribedSubreddits()
        }) match {
        case Some(sa) ⇒
          subscribedSubreddits = sa
          println(s"Starting for subreddits: ${subscribedSubreddits.map(_.name).mkString(",")}")
          if (subscribedSubreddits.nonEmpty) {
            findSuggestedSubreddits(subscribedSubreddits, currDepth)
          } else {
            sender ! DoneMessage(reasonNotCompleted = Some("Could not find any valid subreddits from user or inputted list"))
          }
        case None ⇒
          sender ! DoneMessage(reasonNotCompleted = Some("Please re-run with a valid oauth2 token (use -Dtoken=<token>)"))
      }

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
      if (currDepth < MyUserActor.MAX_DEPTH) {
        println(s"done with depth $currDepth, which is < ${MyUserActor.MAX_DEPTH}")
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

  def findMySubscribedSubreddits()(implicit token: OAuth2BearerToken) = {
    println(s"Using authenticated token: ${token.token}")
    redditService.getSubscribedSubreddits().toSet //get subreddits user is subscribed to
  }

  def findSuggestedSubreddits(subreddits: Set[SubredditData], depth: Int): Unit = {
    println(s"Starting for Depth: $depth, with ${subreddits.size} messages!")
    numMessagesAtDepth += depth → subreddits.size //record # messages sent
    //send a message per subreddit to GARBAGE.SubredditActor
    for (s ← subreddits) context.actorOf(SubredditActor.props) ! SubredditMessage(s, depth)
  }

  def validateSubreddit(name: String): Option[SubredditData] = {
    redditService.validateSubreddit(name)
  }

}
