import akka.actor.ActorSystem

import scala.concurrent.ExecutionContext

class Console {
  import Console._
  import ExecutionContext.Implicits.global


  def start(args: Array[String]): Unit = {
    implicit val system = ActorSystem()

    val redditService = new RedditService
    val authResponse = redditService.authorizeUser()

    for (f ← authResponse) {
      println(f.status)
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
