package controllers

import actors.PageActor
import akka.actor._
import akka.stream.Materializer
import javax.inject._
import play.api.libs.json._
import play.api.libs.ws._
import play.api.mvc._
import websockets.WebSocketUtil

import scala.concurrent.ExecutionContext
import actors.PageRedisActor
import redis.RedisClient
import java.util.concurrent.Future
import play.api.mvc._
import play.libs.streams.ActorFlow
import protocol._
//import protocol.Messages.messageFlowTransformer
@Singleton
class WebSocketController @Inject()(wsClient: WSClient, 
                                    components: ControllerComponents)(implicit system: ActorSystem, ec: ExecutionContext, mat: Materializer) extends AbstractController(components) {

  val redis = RedisClient()
  
  def ws(sid: String) = WebSocketUtil.get[JsValue] { (out: ActorRef) ⇒

    Props(new PageActor(sid, out))
  }


   def wsRedis(sid: String) = WebSocketUtil.getRedis[String] { ( out: ActorRef) ⇒

    Props(new PageRedisActor(redis, out, Seq(sid), Nil))
  }

 
}

