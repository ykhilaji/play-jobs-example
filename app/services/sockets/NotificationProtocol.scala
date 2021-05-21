package services.sockets

import akka.actor.Actor.Receive
import akka.actor.ActorRef
import play.api.libs.json._


object NotificationProtocol extends ProtocolV2 {

  val name: String = "notification"

  case class Notify(content: String, code: Int = 0, protocol: String = "notification") extends ProtocolV2.Payload
  case class TaskOut(data: JsValue,  code: Int = 0, protocol: String = "task") extends ProtocolV2.Payload

  object Notify {
    implicit val jsonFormat: Format[Notify] = Json.format[Notify]
  }

  object TaskOut {
    implicit val taskFormat: Format[TaskOut] = Json.format[TaskOut]
  }

  def receive(out: ActorRef): PartialFunction[Any, Unit] = {

    case notify: String =>
      out ! Notify(notify)

    case data: JsValue =>
      out ! TaskOut(data)
    case a: Any =>
      println("========Notification Unhandled Message=========")
      println(a)
  }: Receive
}
