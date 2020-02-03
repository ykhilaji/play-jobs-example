package controllers

import actors.PageActor
import akka.actor._
import akka.stream.Materializer
import javax.inject._
import play.api.libs.json._
import play.api.mvc._
import websockets.WebSocketUtil

import scala.concurrent.ExecutionContext

@Singleton
class WebSocketController @Inject()(components: ControllerComponents)(implicit system: ActorSystem, ec: ExecutionContext, mat: Materializer) extends AbstractController(components) {

  def ws(sid: String) = WebSocketUtil.get[JsValue] { (out: ActorRef) ⇒

    Props(new PageActor(sid, out))
  }

}

