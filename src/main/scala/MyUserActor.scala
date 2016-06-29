import akka.actor.{Props, ActorLogging, Actor}
import akka.pattern
import spray.http.OAuth2BearerToken

object MyUserActor {

  //recommended way to create a Props for an Actor (http://doc.akka.io/docs/akka/snapshot/scala/actors.html)
  def props = Props(new MyUserActor)

  case class StartMessage(token: Option[String],
                          code: Option[String])

  case class DoneMessage(reasonNotCompleted: Option[String] = None,
                         suggestedSubreddits: List[String] = List.empty)
}

class MyUserActor extends Actor with ActorLogging {
  import MyUserActor._

  val redditService = new RedditService

  override def receive: Receive = {
    case StartMessage(t, c) ⇒
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
          sender ! DoneMessage(suggestedSubreddits = findSuggestedSubreddits())
        case None ⇒
          sender ! DoneMessage(reasonNotCompleted = Some("Please re-run with a valid oauth2 token (use -Dtoken=<token>)"))
      }
    case _ ⇒
      log.error("Invalid Message")
  }

  def findSuggestedSubreddits()(implicit token: OAuth2BearerToken): List[String] = {
    println(s"Using authenticated token: ${token.token}")

//    redditService.getSubscribedSubreddits()

    List.empty
  }
}
