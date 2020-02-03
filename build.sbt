name := """play-jobs"""

version := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SbtWeb)

scalaVersion := "2.12.4"

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-language:postfixOps",
  "-encoding",
  "UTF-8",
  "-feature",
  // "-Ywarn-unused:imports",
  "-Ywarn-dead-code"
)

routesGenerator := InjectedRoutesGenerator

val akkaVersion = "2.5.19"



libraryDependencies ++= Seq(
  ws,
  guice,
  openId,
  filters,
  "com.typesafe.play" %% "play-json" % "2.6.1",
  "com.typesafe.play" %% "play-json-joda" % "2.6.1",
  "com.typesafe.play" %% "play-iteratees" % "2.6.1",
  "com.typesafe.play" %% "play-iteratees-reactive-streams" % "2.6.1"

)

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-cluster" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % akkaVersion

libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
libraryDependencies += "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += "org.awaitility" % "awaitility" % "3.0.0" % Test

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
resolvers += "spray repo" at "http://repo.spray.io"