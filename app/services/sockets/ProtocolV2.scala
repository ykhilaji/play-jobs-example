package services.sockets

import play.api.libs.json._

trait ProtocolV2 {

  def name: String

  def protocolError: JsError = JsError(s"error.ws.protocol.$name")
}

object ProtocolV2 {

  
  trait Payload {

    def code: Int
    def protocol: String
  }

  implicit val payloadFormat: Format[Payload] = new Format[Payload] {
    override def reads(json: JsValue): JsResult[Payload] = (json \ "protocol").validate[String] match {
      case JsSuccess("notification", _) => NotificationProtocol.Notify.jsonFormat.reads(json)
      case JsSuccess("task", _)         => NotificationProtocol.TaskOut.taskFormat.reads(json)
      case _                                       => unknownProtocolError
    }

    override def writes(o: Payload): JsValue = o match {
      case n: NotificationProtocol.Notify => NotificationProtocol.Notify.jsonFormat.writes(n)
      case d: NotificationProtocol.TaskOut => NotificationProtocol.TaskOut.taskFormat.writes(d)
      case _                              => JsNull
    }
  }

  def unknownProtocolError: JsError = JsError(s"error.ws.protocol.unknown")
}