package services

import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy
import java.util.concurrent.{LinkedBlockingQueue, TimeUnit, ThreadPoolExecutor, Executors}
import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import akka.dispatch.ExecutionContexts._
import akka.stream.Materializer
import com.vikram.core.{RedditService, RedditApiWrapper, SubredditSuggesterEngine}

/**
  * Singleton wrapper for the subreddit suggester engine
  */
@Singleton
class SubredditSuggesterEngineProvider @Inject()(actorSystem: ActorSystem)(implicit val mat: Materializer){

  private var instance: SubredditSuggesterEngine = _

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

  def getEngine: SubredditSuggesterEngine = {
    if (instance == null) {
      // pass the play library akka actorSystem implicitly
      implicit val context: ActorSystem = actorSystem

      //todo find best way to insert system properties via scala play
      val redditService = new RedditService(new RedditApiWrapper(
        clientId = sys.props.get("com.vikram.subredditsuggester.client_id"),
        clientSecret = sys.props.get("com.vikram.subredditsuggester.client_secret"),
        redirectUri = sys.props.get("com.vikram.subredditsuggester.redirect_uri")
      ))

      instance = new SubredditSuggesterEngine(redditService)
    }
    instance
  }

}
