import akka.actor._
import akka.pattern
import spray.http.OAuth2BearerToken
import RedditDataModel._

/**
  * This actor is the "start" actor. It finds the subreddits the user is currently subscribed to, then
  * kicks off messages per subscribed subreddit to other actors. It aggregates the results, then sends
  * a complete message back to Main
  */
object MyUserActor {

  //recommended way to create a Props for an Actor (http://doc.akka.io/docs/akka/snapshot/scala/actors.html)
  def props = Props(new MyUserActor)

  case class StartMessage(token: Option[String],
                          code: Option[String])

  case class DoneMessage(reasonNotCompleted: Option[String] = None,
                         suggestedSubreddits: Map[Int, List[SubredditData]] = Map.empty)

  val MAX_DEPTH = sys.props.get("depth").map(_.toInt).getOrElse(3)
}

class MyUserActor extends Actor with ActorLogging {
  import MyUserActor._
  import SubredditActor._

  //todo can RedditService and/or RedditApiWrapper be singletons?
  //the actorsystem and timeout gets implicitly passed to the RedditService and RedditApiWrapper
  implicit val system: ActorSystem = context.system

  val redditService = new RedditService

  private var startActor: Option[ActorRef] = None //reference to start actor

  private var numMessagesAtDepth: Map[Int, Int] = Map.empty //number of messages to process per depth
  private var currDepth = 1
  private var numProcessed = 0 //number of subreddits processed at the current depth

  //todo also keep track of how many of each we see, so we can rank results
  private var subredditsProcessed: Map[Int, List[SubredditData]] = Map.empty //keeps track of subreddits discovered at each depth

  override def receive: Receive = {
    case StartMessage(t, c) ⇒
      startActor = Some(sender)
      //resolve oauth token if possible
      val oauthToken = {
        t.orElse(
          c match {
            case Some(code) ⇒ redditService.oAuthGetToken(code)
            case None ⇒
              redditService.oAuthRequestPermissions()
              None
          }
        ).map(OAuth2BearerToken)
      }

      oauthToken match {
        case Some(oauth) ⇒
          implicit val oAuth2BearerToken = oauth
          findSuggestedSubreddits(findMySubscribedSubreddits(), currDepth)
        case None ⇒
          sender ! DoneMessage(reasonNotCompleted = Some("Please re-run with a valid oauth2 token (use -Dtoken=<token>)"))
      }

    case AnalyzedSubredditMessage(subreddits, depth) ⇒
      //todo hopefully in the future, I can figure out how to not require this condition
      require(depth == currDepth, s"currDepth = $currDepth is not equal to depth = $depth")

      numProcessed += 1
      println(s"numprocessed: $numProcessed, message depth: $depth")
      subredditsProcessed += (subredditsProcessed.get(depth) match {
        case Some(l) ⇒ depth → (l ++ subreddits)
        case None ⇒ depth → subreddits
      })
      `continue?`()

    case FailedSubredditAnalysisMessage(reason, depth) ⇒
      //todo this doesn't fix dead letters
      //uh oh, something went wrong, but should still increment counters so program can terminate
      println(s"Failed to process a subreddit at depth: $depth")
      println(reason.printStackTrace())
      numProcessed += 1
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
        findSuggestedSubreddits(subredditsProcessed.get(currDepth - 1).get, currDepth)
      } else {
        //done
        startActor.foreach(_ ! DoneMessage(suggestedSubreddits = subredditsProcessed))
      }
    }
  }

  def findMySubscribedSubreddits()(implicit token: OAuth2BearerToken) = {
    println(s"Using authenticated token: ${token.token}")
    redditService.getSubscribedSubreddits() //get subreddits user is subscribed to
  }

  def findSuggestedSubreddits(subreddits: List[SubredditData], depth: Int): Unit = {
    println(s"Starting for Depth: $depth, with ${subreddits.size} messages!")
    numMessagesAtDepth += depth → subreddits.size //record # messages sent
    //send a message per subreddit to SubredditActor
    for (s ← subreddits) context.actorOf(SubredditActor.props) ! SubredditMessage(s, depth)
  }

}
