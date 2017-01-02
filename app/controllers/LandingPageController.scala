package controllers

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import play.api.mvc.{Action, Controller}
import services.EngineProvider

import scala.concurrent.ExecutionContext

/**
  * This is the controller for the main home page
  */
@Singleton
class LandingPageController @Inject() (engineProvider: EngineProvider,
                                       actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends Controller {

  def index = Action {
    Ok(views.html.index("vikram"))
  }

  def debug = Action.async {

    val token: Option[String] = None
    val code: Option[String] = None
    val manualSubreddits: Option[Set[String]] = Some(Set("askreddit", "pics"))


    val result = engineProvider.getEngine.debugRun(manualSubreddits = manualSubreddits)


    result.map {
      //    Ok(views.html.results())
      resultString ⇒
        Ok(views.html.results(resultString))
    }

  }
}
