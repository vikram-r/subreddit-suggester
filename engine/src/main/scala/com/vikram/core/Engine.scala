package com.vikram.core

import scala.concurrent.Future

/**
  * Created by vikram on 1/2/17.
  */
trait Engine {
  def run() //todo implement this once you find a good way to load token/code as properties

  def debugRun(token: Option[String] = None,
               code: Option[String] = None,
               manualSubreddits: Option[Set[String]] = None): Future[String]
}
