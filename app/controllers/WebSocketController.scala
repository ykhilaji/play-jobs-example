package controllers

import actors.PageActor
import org.apache.pekko._
import org.apache.pekko.stream.Materializer
import javax.inject._
import play.api.libs.json._
import websockets.WebSocketUtil

import scala.concurrent.ExecutionContext
import play.api.mvc._
import org.apache.pekko.actor.Props
import play.api.libs.ws.WSClient
//import protocol.Messages.messageFlowTransformer
@Singleton
class WebSocketController @Inject()(wsClient: WSClient,
                                    components: ControllerComponents)(
    implicit system: actor.ActorSystem,
    ec: ExecutionContext)
    extends AbstractController(components) {

  def ws(sid: String) = WebSocketUtil.get[JsValue] { (out: actor.ActorRef) =>
    Props(new PageActor(sid, out))
  }

}
