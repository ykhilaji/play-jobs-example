package util

import com.typesafe.config.ConfigFactory

import akka.actor.ActorSystem

import play.api.Play

import javax.inject.Singleton

import akka.stream.ActorMaterializer

object AkkaSupport {
  implicit val system: ActorSystem = ActorSystem(
    "play-jobs",
    ConfigFactory.load("cluster") withFallback Play.current.configuration.underlying
  )

  implicit val materilizer: ActorMaterializer = ActorMaterializer()

}

