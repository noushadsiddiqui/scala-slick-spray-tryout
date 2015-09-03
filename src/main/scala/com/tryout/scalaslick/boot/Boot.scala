package com.tryout.scalaslick.boot

import akka.actor.{Props, ActorSystem}
import akka.io.IO
import com.tryout.scalaslick.config.Configuration
import com.tryout.scalaslick.rest.RestServiceActor
import spray.can.Http

object Boot extends App with Configuration {

  // actor system
  implicit val system = ActorSystem("scala-spray-tryout")

  // rest service actor
  val restService = system.actorOf(Props[RestServiceActor], "rest-endpoint")

  // start HTTP server with rest service actor as a handler
  IO(Http) ! Http.Bind(restService, serviceHost, servicePort)
}