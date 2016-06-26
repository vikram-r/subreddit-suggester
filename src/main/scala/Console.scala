import spray.http.OAuth2BearerToken

import scala.concurrent.Promise

class Console {

  val redditService = new RedditService

  def start(args: Array[String]): Unit = {
    //todo still a mess
    val oauthToken = {
      sys.props.get("token").orElse(
        sys.props.get("code") match {
          case Some(code) ⇒ redditService.oAuthGetToken(code)
          case None ⇒
            redditService.oAuthRequestPermissions()
            None
        }
      ).map(OAuth2BearerToken)
    }

    if (oauthToken.isEmpty) {
      println("Please re-run with a valid oauth2 token")
      System.exit(0)
    }

    implicit val oAuth2BearerToken = oauthToken.get
    redditService.getSubscribedSubreddits()
  }
}

object Console {
  //static main for gradle entry point
  def main(args: Array[String]): Unit = {
    val console = new Console
    console.start(args)
  }
}
