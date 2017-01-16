package controllers

import java.util.UUID
import javax.inject.{Inject, Singleton}

import play.api.mvc.{Action, Controller}
import services.SubredditSuggesterEngineProvider

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by vikram on 1/15/17.
  */
@Singleton
class OAuth2Controller @Inject() (engineProvider: SubredditSuggesterEngineProvider)(implicit exec: ExecutionContext) extends Controller {
  import OAuth2Controller._

  val redditService = engineProvider.getEngine.redditService

  // This endpoint should be identical to the callback url set for the Reddit App
  def callback(code: Option[String], state: Option[String]) = Action.async {
    (for {
      c ← code
      s ← state
    } yield {
      redditService.oAuthGetToken(c) map {
        token ⇒
          Redirect(routes.LandingPageController.index()).withSession(SESSION_TOKEN_KEY → token, SESSION_AUTH_STATE_KEY → s)
      }
    }).getOrElse {
      Future.successful(BadRequest("Something went wrong. I should handle this better..."))
    }
  }

}

object OAuth2Controller {
  val SESSION_TOKEN_KEY = "token"
  val SESSION_AUTH_STATE_KEY = "auth_state"
}