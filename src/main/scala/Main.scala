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

    implicit val timeout = Timeout(30, TimeUnit.MINUTES)
    implicit val executionContext = global

    val context = ActorSystem("System")

    val myUserActor = context.actorOf(MyUserActor.props, "myUserActor")
    val done = myUserActor ? StartMessage(
      token = sys.props.get("token"),
      code = sys.props.get("code"),
      manualSubreddits = sys.props.get("subreddits").map(_.split(',').toSet)
    )

    for (result ← done) {
      result match {
        case DoneMessage(None, r) ⇒
          prettyPrintResults(r)

        case DoneMessage(Some(e), r) ⇒ println(e) //terminated early
      }

      context.terminate()
    }
  }

  def prettyPrintResults(results: Map[Int, Map[SubredditData, Int]]): Unit = {
    import CounterMapHelper._
    println("~~~~RESULTS~~~~")
    val lineSep = "\n-----------\n"
    println((for (depth ← results.keySet) yield {
        s"Depth: $depth$lineSep" + (
          for {
            subredditMap ← results.get(depth).toSeq
            (s, c) ← subredditMap.toList.sortBy(_._2).reverse
          } yield s"${s.name} - $c").mkString("\n")
      }).mkString(lineSep))


    println("~~~~SUMMARY~~~~")
    println(results.aggregateCounterMaps.toList.sortBy(_._2).reverse.map(t ⇒ t._1.name + s" (${t._2})").mkString("\n"))
  }
}
