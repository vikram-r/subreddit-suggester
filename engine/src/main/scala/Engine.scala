import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Props}
import akka.dispatch.ExecutionContexts._
import akka.pattern.ask
import akka.util.Timeout
import RedditDataModel._

import scala.concurrent.Future

trait Engine {
  def run() //todo implement this once you find a good way to load token/code as properties

  def debugRun(token: Option[String] = None,
               code: Option[String] = None,
               manualSubreddits: Option[Set[String]] = None): Future[String]
}

class ScalaPlayEngine(implicit val actorSystem: ActorSystem) extends Engine {

  override def run(): Unit = ???

  //static main for gradle entry point
  override def debugRun(token: Option[String], code: Option[String], manualSubreddits: Option[Set[String]]): Future[String] = {
    import MyUserActor._

    implicit val timeout = Timeout(30, TimeUnit.MINUTES)
    implicit val executionContext = global

    val myUserActor = actorSystem.actorOf(MyUserActor.props, "myUserActor")
    val done = myUserActor ? StartMessage(
      token = token,
      code = code,
      manualSubreddits = manualSubreddits
    )

    for (result ← done) yield {
      result match {
        case DoneMessage(None, r) ⇒
          val result = prettyPrintString(r)
          prettyPrintResults(result)
          result
        case DoneMessage(Some(e), r) ⇒
          println(e) //terminated early
          ""
      }
      //todo is this correct? If the actorSystem passed in is the Play system, then I think Play handles the lifecycle
//      actorSystem.terminate()
    }
  }

  def prettyPrintString(results: Map[Int, Map[SubredditData, Int]]): String = {
    import CounterMapHelper._
    val lineSep = "\n-----------\n"
    println((for (depth ← results.keySet) yield {
        s"Depth: $depth$lineSep" + (
          for {
            subredditMap ← results.get(depth).toSeq
            (s, c) ← subredditMap.toList.sortBy(_._2).reverse
          } yield s"${s.name} - $c").mkString("\n")
      }).mkString(lineSep))

    results.aggregateCounterMaps.toList.sortBy(_._2).reverse.map(t ⇒ t._1.name + s" (${t._2})").mkString("\n")
  }

  def prettyPrintResults(resultString: String): Unit = {
    println("~~~~RESULTS~~~~")
    println("~~~~SUMMARY~~~~")
    println(resultString)
  }
}
