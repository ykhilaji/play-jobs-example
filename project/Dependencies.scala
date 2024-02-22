import sbt._

object Dependencies {

  object db {
    val version = "2.6.10"
    val anorme = "org.playframework.anorm" %% "anorm" % version
  }

  object enum {
    val version = "3.1"
    val core = "org.julienrf" %% "enum" % version
  }

  object play {
    val version = "2.8.1"
    val json = "com.typesafe.play" %% "play-json" % version
    val json_joda = "com.typesafe.play" %% "play-json-joda" % version
   /*  val iteratees = "com.typesafe.play" %% "play-iteratees" % "2.6.1"
    val reactive_streams = "com.typesafe.play" %% "play-iteratees-reactive-streams" % "2.6.1" */
    //val request_tracer = "com.alexitc" %% "play-request-tracer" % "0.1.0"
    val json_extensions = "ai.x" %% "play-json-extensions" % "0.42.0"

  }

  object circuitBreaker {
    val version = "1.0.6"
    val core = "com.hootsuite" %% "scala-circuit-breaker" % version
  }
  object mockito {
    val version = "2.28.2"
    val core = "org.mockito" % "mockito-core" % version
  }

  object scalatest {
    val version = "5.1.0"
    val play = "org.scalatestplus.play" %% "scalatestplus-play" % version
  }

  object jsMessages {
    val version = "5.0.0"
    val core = "org.julienrf" %% "play-jsmessages" % version
  }

  object posgtres {
    val version = "9.4.1212"
    val driver = "org.postgresql" % "postgresql" % version
  }

  object pdfbox {
    val version = "1.8.16"
    val core = "org.apache.pdfbox" % "pdfbox" % version
  }

  object scalaz {
    val version = "7.3.6"
    val core = "org.scalaz" %% "scalaz-core" % version
  }

  object scalactic {
    val version = "3.0.4"
    val core = "org.scalactic" %% "scalactic" % version
  }

  object opencvs {
    val version = "5.7.0"
    val core = "com.opencsv" % "opencsv" % version
  }

  object HikariCP {
    val version = "2.7.5"
    val core = "com.zaxxer" % "HikariCP" % version
  }

  object akka {
    val version = "2.6.20"
    val kryo_serialization = "io.altoo" %% "akka-kryo-serialization" % "2.4.3"
    val testkit = "com.typesafe.akka" %% "akka-testkit" % version
    val actor = "com.typesafe.akka" %% "akka-actor" % version
    val slf4j = "com.typesafe.akka" %% "akka-slf4j" % version
    val cluster = "com.typesafe.akka" %% "akka-cluster" % version
    val cluster_tools = "com.typesafe.akka" %% "akka-cluster-tools" % version
    val stream = "com.typesafe.akka" %% "akka-stream" % version
    val jackson = "com.typesafe.akka" %% "akka-serialization-jackson" % version
    val akka_typed = "com.typesafe.akka" %% "akka-actor-typed" % version
    val akka_cluster_typed = "com.typesafe.akka" %% "akka-cluster-typed" % version

//    val coordination = "com.typesafe.akka" %% "akka-coordination" % version
 //   val remote = "com.typesafe.akka" %% "akka-remote" % version
 //   val protobuf = "com.typesafe.akka" %% "akka-protobuf-v3" % version
   // val pki = "com.typesafe.akka" %% "akka-pki" % version
  }

  object commonIo {
    val version = "2.11.0"
    val core = "commons-io" % "commons-io" % version
  }

  object kamon {
    val version = "2.5.8"
    val bundele = "io.kamon" %% "kamon-bundle" % version
    val prometheus = "io.kamon" %% "kamon-prometheus" % version
  }

}
