
import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import scala.concurrent.Future
import spray.can.Http
import spray.http._
import HttpMethods._
import akka.util.Timeout

object RedditService {
  val BASE_URL = "https://www.reddit.com/api/v1/"
  val CLIENT_ID = "G7_Lv9YNO8oIDA"
  val REDIRECT_URI = "https://www.google.com"

  implicit val system = ActorSystem()
  implicit val timeout = Timeout(30, TimeUnit.SECONDS)

}


class RedditService {
  import RedditService._


  def authorizeUser(scope: List[String] = List("history")): Future[HttpResponse] = {
    val authState = UUID.randomUUID()
    val duration = "temporary"
    val url = BASE_URL +
      s"authorize?client_id=$CLIENT_ID&response_type=code&state=$authState&" +
      s"redirect_uri=$REDIRECT_URI&duration=$duration&scope=${scope.mkString(" ")}"

    (IO(Http) ? HttpRequest(GET, Uri(url))).mapTo[HttpResponse]
  }


}
