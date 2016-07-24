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

  val MAX_DEPTH = sys.props.get("depth").map(_.toInt).getOrElse(5)
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
//      require(currDepth == depth, s"currDepth = $currDepth, depth = $depth")
      numProcessed += 1
      println(s"numprocessed: $numProcessed, message depth: $depth")
      subredditsProcessed += (subredditsProcessed.get(depth) match {
        case Some(l) ⇒ depth → (l ++ subreddits)
        case None ⇒ depth → subreddits
      })

      //check if we're done processing at this depth
      if (numProcessed >= numMessagesAtDepth.getOrElse(depth, 0)) {
        if (currDepth < MyUserActor.MAX_DEPTH) {
          println(s"done with depth $currDepth, which is < ${MyUserActor.MAX_DEPTH}")
          numProcessed = 0
          currDepth += 1
          findSuggestedSubreddits(subredditsProcessed.get(depth).get, currDepth)
        } else {
          //done
          startActor.foreach(_ ! DoneMessage(suggestedSubreddits = subredditsProcessed))
        }
      }
    case _ ⇒
      log.error("Invalid Message")
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
