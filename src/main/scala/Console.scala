
class Console {

  val redditService = new RedditService

  def start(args: Array[String]): Unit = {

    //this is kind of a disaster, but it's temporary until I move this to a webserver
    (sys.props.get("code"), sys.props.get("token")) match {
      case (Some(code), Some(token)) ⇒ //todo do subreddit logic
      case (None, Some(token)) => //todo do subreddit logic
      case (Some(code), None) ⇒
        redditService.oAuthGetToken(code)
      case _ ⇒
        redditService.oAuthRequestPermissions()
    }
  }
}

object Console {
  //static main for gradle entry point
  def main(args: Array[String]): Unit = {
    val console = new Console
    console.start(args)
  }
}
