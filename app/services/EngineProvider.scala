package services

import javax.inject.Singleton
//import _root_.Engine
//import _root_.ScalaPlayEngine

/**
  * Singleton wrapper for the subreddit suggester engine
  */
@Singleton
class EngineProvider {

  var instance: Engine = _

  def getEngine: engine.Engine = {
    if (instance == null) {
      // pass the play library akka actorSystem implicitly
      import play.api.libs.concurrent.Akka._
      instance = new ScalaPlayEngine()
    }
    instance
  }

}
