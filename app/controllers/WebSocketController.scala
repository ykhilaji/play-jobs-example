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
import play.api.mvc._
import play.libs.streams.ActorFlow
import protocol._
import play.api.Logger
import services.sockets._
import play.api.mvc.WebSocket.MessageFlowTransformer
import scala.util.Try
import services.sockets.ProtocolV2._
import scala.concurrent.Future
import scala.concurrent.duration._

import sockets._

@Singleton
class WebSocketController @Inject()(wsClient: WSClient, 
                                    components: ControllerComponents)(implicit system: ActorSystem, ec: ExecutionContext, mat: Materializer) extends AbstractController(components) {
  
  val logger: Logger = play.api.Logger(getClass)

  def ws(sid: String) = WebSocketUtil.get[JsValue] { (out: ActorRef) ⇒
    Props(new PageActor(sid, out)) 
  }



  implicit def userWebSocketMessageFlowTransformer: WebSocket.MessageFlowTransformer[Try[Payload], Payload] =
    caseClassMessageFlowTransformer[Payload, Payload]

  def ws2(sid: String): WebSocket = WebSocket.acceptOrResult[Try[Payload], Payload] { request =>
   val _sid = Option(sid)
    Future.successful{
      _sid match {
        case Some(uid) => WebSocketFlow.actorRef[Try[Payload], Payload](out => UserWebSocket.props(uid, out), name = Some(uid))
        case _      => throw new Exception("No auth")
      }
    }.map(flow => Right(flow)).recover {
      case e: Exception =>
        logger.error("Cannot connection websocket", e)
        Left(BadRequest(Json.obj("error" -> "Cannot connection websocket")))
    }
  }

  def connect: WebSocket = WebSocket.acceptOrResult[Try[Payload], Payload] { request =>
    Future.successful{
     request.getQueryString("userId")  match {
        case Some(uid) => WebSocketFlow.actorRef[Try[Payload], Payload](out => UserWebSocket.props(uid, out), name = Some(uid))
        case None      => throw new Exception("No auth")
      }
    }.map(flow => Right(flow)).recover {
      case e: Exception =>
        logger.error("Cannot connection websocket", e)
        Left(BadRequest(Json.obj("error" -> "Cannot connection websocket")))
    }
  }

  
 
}

