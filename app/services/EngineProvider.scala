package services

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import com.vikram.core.{Engine, ScalaPlayEngine}

/**
  * Singleton wrapper for the subreddit suggester engine
  */
@Singleton
class EngineProvider @Inject() (actorSystem: ActorSystem){

  var instance: Engine = _

  def getEngine: Engine = {
    if (instance == null) {
      // pass the play library akka actorSystem implicitly
      implicit val context: ActorSystem = actorSystem
      instance = new ScalaPlayEngine()
    }
    instance
  }

}