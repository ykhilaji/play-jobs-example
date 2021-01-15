package websockets

import akka.actor._
import akka.stream.Materializer
import play.api.libs.json.JsValue
import play.api.libs.streams.ActorFlow

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import play.api.mvc.WebSocket

import scala.reflect.ClassTag



object WebSocketUtil {
  
  def get[T: ClassTag](props: (ActorRef) ⇒ Props)(implicit system: ActorSystem, ec: ExecutionContext,mat: Materializer): WebSocket = {
    WebSocket.acceptOrResult[JsValue, JsValue] { implicit request =>
     Future.successful(Right(ActorFlow.actorRef((out: ActorRef) ⇒ props(out))))

    }
  }

    def getRedis[T: ClassTag](props: (ActorRef) ⇒ Props)(implicit system: ActorSystem, ec: ExecutionContext,mat: Materializer): WebSocket = {
    WebSocket.acceptOrResult[String, String] { implicit request =>
      println(s"==========${request.queryString("channel")}")
     Future.successful(Right(ActorFlow.actorRef((out: ActorRef) ⇒ props(out))))

    }
  }
}
