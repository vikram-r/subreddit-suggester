package services

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.vikram.core.{RedditService, RedditApiWrapper, Engine, ScalaPlayEngine}

/**
  * Singleton wrapper for the subreddit suggester engine
  */
@Singleton
class EngineProvider @Inject() (actorSystem: ActorSystem) (implicit val mat: Materializer){

  private var instance: Engine = _

  def getEngine: Engine = {
    if (instance == null) {
      // pass the play library akka actorSystem implicitly
      implicit val context: ActorSystem = actorSystem

      //todo find best way to insert system properties via scala play
      val redditService = new RedditService(new RedditApiWrapper(
        clientId = sys.props.get("com.vikram.subredditsuggester.client_id"),
        clientSecret = sys.props.get("com.vikram.subredditsuggester.client_secret"),
        redirectUri = sys.props.get("com.vikram.subredditsuggester.redirect_uri")
      ))

      instance = new ScalaPlayEngine(redditService)
    }
    instance
  }

}
