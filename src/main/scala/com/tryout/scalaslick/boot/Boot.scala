package com.tryout.scalaslick.boot

import akka.actor.{Props, ActorSystem}
import akka.io.IO
import com.tryout.scalaslick.config.Configuration
import com.tryout.scalaslick.rest.RestServiceActor
import spray.can.Http

object Boot extends App with Configuration {

  // create an actor system for application
  implicit val system = ActorSystem("rest-service-tryout")

  // create and start rest service actor
  val restService = system.actorOf(Props[RestServiceActor], "rest-endpoint")

  // start HTTP server with rest service actor as a handler
  IO(Http) ! Http.Bind(restService, serviceHost, servicePort)
}