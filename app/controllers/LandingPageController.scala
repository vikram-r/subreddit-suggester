package controllers

import java.util.UUID
import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import play.api.mvc.{Action, Controller}
import services.OAuthSubredditSuggesterEngineProvider
import OAuth2Controller._

import scala.concurrent.{Future, ExecutionContext}

/**
  * This is the controller for the main home page
  */
@Singleton
class LandingPageController @Inject() (engineProvider: OAuthSubredditSuggesterEngineProvider,
                                       actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends Controller {

  def index = Action { implicit request ⇒

    //todo no need to display token on the page (testing purposes only)
    val token = request.session.get(SESSION_TOKEN_KEY)
    val loginUrl = engineProvider.getEngine.redditService.oAuthUrl(UUID.randomUUID)
    val logoutUrl = routes.OAuth2Controller.logout().absoluteURL()

    Ok(views.html.index(loginUrl, logoutUrl, token))
  }

  def debug = Action.async {
    val manualSubreddits: Set[String] = Set("askreddit", "pics")
    val result = engineProvider.getEngine.run(manualSubreddits)
    result.map { resultString ⇒
        Ok(views.html.results(resultString))
    }
  }

  def oauthDebugRun = Action.async { implicit request ⇒
    request.session.get(SESSION_TOKEN_KEY) match {
      case Some(token) ⇒
        engineProvider.getEngine.runWithOauthToken(token).map {
          result ⇒
            Ok(views.html.results(result))
        }
      case None ⇒
        Future.successful(BadRequest(views.html.results("You are not logged in!")))
    }
  }
}
