name := """play-jobs"""

version := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SbtWeb,JavaAppPackaging,DockerComposePlugin)

scalaVersion := "2.12.11"

javaOptions in Universal ++= Seq(
  "-Dpidfile.path=/dev/null"
)

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
  "com.typesafe.play" %% "play-iteratees-reactive-streams" % "2.6.1",
  "com.pauldijou" %% "jwt-play" % "0.19.0",
  "com.pauldijou" %% "jwt-core" % "0.19.0",
  "com.auth0" % "jwks-rsa" % "0.6.1" 

)

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-cluster" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % akkaVersion
