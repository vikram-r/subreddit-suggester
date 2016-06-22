
class Console {

  val redditService = new RedditService

  def start(args: Array[String]): Unit = {
    redditService.commandLineOAuthLogin()
  }
}

object Console {
  //static main for gradle entry point
  def main(args: Array[String]): Unit = {
    val console = new Console
    console.start(args)
  }
}
