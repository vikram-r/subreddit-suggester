import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Props}
import akka.dispatch.ExecutionContexts._
import akka.pattern.ask
import akka.util.Timeout

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
        case DoneMessage(None, r) ⇒ println(r) //results
        case DoneMessage(Some(e), r) ⇒ println(e) //terminated early
      }

      println("here")
      context.terminate()
    }
  }
}
