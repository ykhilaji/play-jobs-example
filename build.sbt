name := """play-jobs"""

version := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala, JavaAgent, SbtWeb)

scalaVersion := "2.12.11"

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

lazy val akkaVersion = "2.6.10"
lazy val kamonVersion = "2.1.7"
lazy val akkaManagementVersion =  "1.0.5"

libraryDependencies ++= Seq(
  ws,
  guice,
  openId,
  filters,
  "com.typesafe.play" %% "play-json" % "2.6.1",
  "com.typesafe.play" %% "play-json-joda" % "2.6.1",
  "com.typesafe.play" %% "play-iteratees" % "2.6.1",
  "org.julienrf" %% "enum" % "3.1",
  "ai.x" %% "play-json-extensions" % "0.10.0",
  "io.altoo" %% "akka-kryo-serialization" % "1.1.5",
  "io.kamon" %% "kamon-bundle" % "2.0.4",
  "io.kamon" %% "kamon-prometheus" % "2.0.1",
  "com.github.etaty" %% "rediscala" % "1.8.0",
  "com.sandinh" %% "paho-akka" % "1.6.0",
  "com.typesafe.play" %% "play-iteratees-reactive-streams" % "2.6.1"
)

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-cluster" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % akkaVersion
libraryDependencies +="com.lightbend.akka.management" %% "akka-management" % akkaManagementVersion
libraryDependencies +="com.lightbend.akka" %% "akka-diagnostics" % "1.1.16"

