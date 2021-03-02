package model

import core.JsonImplicits.{dateReads, enumReads, enumWrites}

import enum.Enum
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.mvc.QueryStringBindable
import org.joda.time.DateTime
import model._


case class TaskInfra(sid: String , info: String , data: JsValue , task: Option[TaskModel]) extends protocol.PlayJsonSerializer 

case class TaskModel(
                      id: Long,
                      userId: String,
                      userData: Option[JsValue], 
                      jobId: Long, 
                      status: TaskStatus, 
                      data: JsValue, 
                      taskType: TaskType, 
                      response: Option[JsValue], 
                      visible : Boolean, 
                      date: Option[DateTime],
                      filename: Option[String], 
                      lineNumber: Option[Int], 
                      printResults : Option[JsValue]
) extends protocol.PlayJsonSerializer

object TaskModel {
  type TaskId = Long
  type UserId = String
  type JobId = Long

 

  // Objects/methods used to parse and create JSON
  implicit val taskReads =
    (
      (__ \ "id").read[TaskId] and
        (__ \ "user_id").read[UserId] and
        (__ \ "user_data").readNullable[JsValue] and
        (__ \ "job_id").read[JobId] and
        (__ \ "status").read[TaskStatus] and
        (__ \ "data").read[JsValue] and
        (__ \ "type").read[TaskType] and
        (__ \ "response").readNullable[JsValue] and
        (__ \ "visible").read[Boolean] and
        (__ \ "date").readNullable[DateTime] and
        (__ \ "filename").readNullable[String] and
        (__ \ "line_number").readNullable[Int] and
        (__ \ "print_results").readNullable[JsValue]
      ) (TaskModel.apply _)

  implicit val taskWrites = OWrites[TaskModel] { task =>
    Json.obj(
      "id" -> task.id,
      "data" -> task.data,
      "response" -> task.response,
      "visible" -> task.visible,
      "filename" -> task.filename,
      "lineNumber" -> task.lineNumber,
      "status" -> task.status,
      "type" -> task.taskType,
      "jobId" -> task.jobId,
      "print_results" -> task.printResults
    )
  }

}


object TaskInfra {

  implicit val taskReads = 
   (
      (__ \ "sid").read[String] and
      (__ \ "info").read[String] and
      (__ \ "data").read[JsValue] and 
      (__ \ "task").readNullable[TaskModel] 
   ) (TaskInfra.apply _)
  
  implicit val taskWrites = OWrites[TaskInfra] { task =>
    Json.obj(
      "sid" -> task.sid,
      "info" -> task.info,
      "data" -> task.data, 
      "task" -> task.task
    )
  }    
 }




sealed trait TaskStatus

object TaskStatus {

  case object Submitted extends TaskStatus

  case object Successful extends TaskStatus

  case object BusinessError extends TaskStatus

  case object TechnicalError extends TaskStatus

  case object Cancelled extends TaskStatus

  case object Warning extends TaskStatus

  implicit val enum: Enum[TaskStatus] = Enum.derived[TaskStatus]
}


sealed trait TaskType

object TaskType {

  case object GenerateLabel extends TaskType

  case object CheckGenerateLabel extends TaskType

  case object MergePdf extends TaskType

  case object PrintAuto extends TaskType

  case object PrintAutoCmd extends TaskType

  case object PrintUnite extends TaskType

  case object PrintHistory extends TaskType

  case object ImportFile extends TaskType

  case object AutoCodeProduitCorrection  extends TaskType

  val desktopTasks: Set[TaskType] = Set(PrintAuto, PrintUnite, PrintHistory, ImportFile)

  implicit val enum: Enum[TaskType] = Enum.derived[TaskType]


  implicit def printerTypeQueryStringBinder(implicit stringBinder: QueryStringBindable[String]) = new QueryStringBindable[TaskType] {

    def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, TaskType]] =
      params.get(s"${key}") match {
        case Some(Seq(str)) => enum.decodeOpt(str).map(Right(_))
        case _ => None
      }

    def unbind(key: String, printerType: TaskType): String =
      stringBinder.unbind(key, enum.encode(printerType))
  }
}
