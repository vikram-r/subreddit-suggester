import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Props}
import akka.dispatch.ExecutionContexts._
import akka.pattern.ask
import akka.util.Timeout
import RedditDataModel._

object Main {
  //static main for gradle entry point
  def main(args: Array[String]): Unit = {
    import MyUserActor._

    val token = sys.props.get("token")
    val code = sys.props.get("code")

    implicit val timeout = Timeout(30, TimeUnit.SECONDS)
    implicit val executionContext = global

    val context = ActorSystem("System")

    val myUserActor = context.actorOf(MyUserActor.props, "myUserActor")
    val done = myUserActor ? StartMessage(token, code)

    for (result ← done) {
      result match {
        case DoneMessage(None, r) ⇒
          println("DONE!")
          prettyPrintResults(r)
          //todo make output pretty

        case DoneMessage(Some(e), r) ⇒ println(e) //terminated early
      }

      context.terminate()
    }
  }

  def prettyPrintResults(results: Map[Int, List[SubredditData]]): Unit = {
    println("~~~~RESULTS~~~~")
    println(results.keys)
    for {
      depth ← results.keys
      subreddits ← results.get(depth)
      s = subreddits.distinct.map(_.name)
    } {
      println(s"Depth $depth: ${s.mkString(",")}")
    }
  }
}
