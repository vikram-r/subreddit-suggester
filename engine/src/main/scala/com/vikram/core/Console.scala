package com.vikram.core

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext

/**
  * Entry point for CLI
  */
object Console extends App {

  val subreddits = args.toSet

  if (subreddits.nonEmpty) {

    implicit val ec = ExecutionContext.global
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    val engine = new SubredditSuggesterEngine(new RedditService(new RedditApiWrapper()))
    engine.run(subreddits)
  } else {
    println("Pass subreddits as java parameters")
  }
}
