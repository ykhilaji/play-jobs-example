package websockets
import akka.actor._
import akka.stream._
import play.api.Logger
import play.api.libs.json._
import play.api._
import play.api.mvc._


import scala.concurrent._
import scala.util.Try
import scala.reflect.ClassTag
import services.sockets._
import play.api.libs.streams.ActorFlow

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
