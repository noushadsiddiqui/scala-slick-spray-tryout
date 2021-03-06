package com.tryout.scalaslick.config

import com.typesafe.config.ConfigFactory
import util.Try

/**
 * Holds service configuration settings.
 */
trait Configuration {

  val config = ConfigFactory.load()
  lazy val serviceHost = Try(config.getString("service.host")).getOrElse("localhost")
  lazy val servicePort = Try(config.getInt("service.port")).getOrElse(8080)
  lazy val dbHost = Try(config.getString("db.host")).getOrElse("localhost")
  lazy val dbPort = Try(config.getInt("db.port")).getOrElse(3306)
  lazy val dbName = Try(config.getString("db.name")).getOrElse("azuga")
  lazy val dbUser = Try(config.getString("db.user")).getOrElse("root")
  lazy val dbPassword = Try(config.getString("db.password")).toOption.orNull
}
