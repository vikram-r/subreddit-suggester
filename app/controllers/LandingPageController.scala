package controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc.{Action, Controller}

/**
  * This is the controller for the main home page
  */
@Singleton
class LandingPageController @Inject extends Controller {

  def index = Action {

    Ok(views.html.index("vikram"))
  }
}
