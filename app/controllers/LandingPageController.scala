package controllers

import java.util.UUID
import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import play.api.mvc.{Action, Controller}
import services.SubredditSuggesterEngineProvider
import OAuth2Controller._

import scala.concurrent.ExecutionContext

/**
  * This is the controller for the main home page
  */
@Singleton
class LandingPageController @Inject() (engineProvider: SubredditSuggesterEngineProvider,
                                       actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends Controller {

  def index = Action { implicit request ⇒

    //todo no need to display token on the page (testing purposes only)
    val token = request.session.get(SESSION_TOKEN_KEY)
    val loginUrl = engineProvider.getEngine.redditService.oAuthUrl(UUID.randomUUID)

    Ok(views.html.index(loginUrl, token))
  }

  def debug = Action.async {
    val token: Option[String] = None
    val code: Option[String] = None
    val manualSubreddits: Option[Set[String]] = Some(Set("askreddit", "pics"))


    val result = engineProvider.getEngine.debugRun(manualSubreddits = manualSubreddits)


    result.map { resultString ⇒
        Ok(views.html.results(resultString))
    }
  }
}
