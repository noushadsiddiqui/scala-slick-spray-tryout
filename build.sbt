name := "scala-slick-spray-tryout"

version := "1.0"

scalaVersion := "2.11.5"

libraryDependencies ++= Seq(
    "io.spray" % "spray-can" % "1.3.3",
    "io.spray" % "spray-http" % "1.3.3",
    "io.spray" % "spray-routing" % "1.3.3",
    "net.liftweb" %% "lift-json" % "2.5.1",
    "com.typesafe.slick" %% "slick" % "3.0.2",
    "mysql" % "mysql-connector-java" % "5.6.21",
    "com.typesafe.akka" %% "akka-actor" % "1.3.3",
    "com.typesafe.akka" %% "akka-slf4j" % "1.3.3",
    "ch.qos.logback" % "logback-classic" % "1.0.13"
)

resolvers ++= Seq(
    "Spray repository" at "http://repo.spray.io",
    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)

assemblySettings
