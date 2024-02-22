addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.4.0")


// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.16")
// To keep an homogeneous code style
addSbtPlugin("com.lucidchart" % "sbt-scalafmt" % "1.14")
//addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.0")

// Add build infos into code
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.7.0")

// plugin to manage release cycle
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.6")

// static code analysis
addSbtPlugin("com.sksamuel.scapegoat" %% "sbt-scapegoat" % "1.0.7")

// assets pipeline
addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.4")
addSbtPlugin("com.typesafe.sbt" % "sbt-gzip" % "1.0.2")

addSbtPlugin("com.tapad" % "sbt-docker-compose" % "1.0.34")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.11.1")

addSbtPlugin("com.lightbend.sbt" % "sbt-javaagent" % "0.1.6")
