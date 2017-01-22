package services

import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy
import java.util.concurrent.{LinkedBlockingQueue, TimeUnit, ThreadPoolExecutor, Executors}
import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import akka.dispatch.ExecutionContexts._
import akka.stream.Materializer
import com.vikram.core._
import play.Configuration

/**
  * Singleton wrapper for the subreddit suggester engine
  */
@Singleton
class OAuthSubredditSuggesterEngineProvider @Inject()(actorSystem: ActorSystem, conf: Configuration)(implicit val mat: Materializer){

  private var instance: OAuthSubredditSuggesterEngine = _

  //todo this will almost definitely need tuning
  // implicitly pass a separate execution context to the io heavy engine
//  implicit val ec = {
//    val numThreads = 10
//    val queueSize = 100
//
//    ExecutionContext.fromExecutorService(
//      new ThreadPoolExecutor(
//        numThreads,
//        numThreads,
//        2L,
//        TimeUnit.MINUTES,
//        new LinkedBlockingQueue[Runnable](queueSize),
//        CallerRunsPolicy
//      )
//    )
//  }
  //todo for now using Akka execution context. It might be necessary to use a separate one for the actual io work
  implicit val ec = global

  def getEngine: OAuthSubredditSuggesterEngine = {
    if (instance == null) {
      // pass the play library akka actorSystem implicitly
      implicit val context: ActorSystem = actorSystem

      //todo what happens if these system vars are not set?
      val oauth2RedditService = new OAuthRedditService(new RedditOauth2ApiWrapper {
        override val CLIENT_ID: String = conf.getString("com.vikram.subredditsuggester.client_id")
        override val CLIENT_SECRET: String = conf.getString("com.vikram.subredditsuggester.client_secret")
        override val REDIRECT_URI: String = conf.getString("com.vikram.subredditsuggester.redirect_uri")
      })

      instance = new OAuthSubredditSuggesterEngine(oauth2RedditService)
    }
    instance
  }

}
