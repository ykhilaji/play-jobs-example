package protocol

import model.TaskInfra


  sealed trait MsgIn extends core.ActorProtocol
  sealed trait MsgOut extends core.ActorProtocol


  
trait JSFormatter {
  def identifier = getClass.getSimpleName.dropRight(1)
}

object Messages {

  import play.api.libs.json._
  /**
  implicit object Writes extends Writes[MsgOut] {
    override def writes(o: MsgOut): JsValue = {
      o match {
        case i: TaskOut => TaskOut.formatTaskOut.writes(i)
        case x => sys.error("type not found " + o)
      }
    }
  }

   implicit object Reads extends Reads[MsgIn] {
    override def reads(json: JsValue): JsResult[MsgIn] = {
      val msgType = (json \ "msg").as[String]
      msgType match {
        case "taskComplete" => TaskComplete.formatTaskComplte.reads(json)
      }
    }
  }

  **/

  final case class TaskComplete(task: TaskInfra)
  final case class TaskOut(task: TaskInfra)
  
  /** 
  object TaskOut extends JSFormatter {
   implicit val formatTaskOut = Json.format[TaskOut]
  }

  object TaskComplete extends JSFormatter {
   implicit val formatTaskComplte = Json.format[TaskComplete]
  }


  import play.api.mvc.WebSocket.MessageFlowTransformer

  implicit val messageFlowTransformer = MessageFlowTransformer.jsonMessageFlowTransformer[MsgIn,MsgOut]
**/
  
}
