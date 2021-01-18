package core

import javax.inject._
import play.api.Configuration
import play.api.http.HttpConfiguration
import play.Environment

@Singleton
class Conf @Inject()(conf: Configuration, httpConfig: HttpConfiguration , env : Environment) {

    private def getBoolean(key: String) = conf.getOptional[Boolean](key).getOrElse(sys.error(s"Missing config key: $key"))

    object websocket {
      val redisEnabled = getBoolean("websocket.redis.enabled")
  }

}
