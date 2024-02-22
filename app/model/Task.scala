package model

import enum.Enum
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.mvc.QueryStringBindable
import org.joda.time.DateTime
import core.JsonImplicits.{dateReads, enumReads, enumWrites}
import org.eclipse.jetty.util.thread.Scheduler.Task

case class TaskInfra(sid: String,
                     info: String,
                     data: JsValue,
                     task: Option[TaskModel])

case class TaskModel(
    id: Long,
    userId: String,
    userData: Option[JsValue],
    jobId: Long,
    status: TaskStatus,
    data: JsValue,
    taskType: TaskType,
    response: Option[JsValue],
    visible: Boolean,
    date: Option[DateTime],
    filename: Option[String],
    lineNumber: Option[Int],
    printResults: Option[JsValue]
)

object TaskModel {
  type TaskId = Long
  type UserId = String
  type JobId = Long

  // Objects/methods used to parse and create JSON
  implicit val taskReads: Reads[TaskModel] =
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
    )(TaskModel.apply _)

  implicit val taskWrites: Writes[TaskModel] = OWrites[TaskModel] { task =>
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

  implicit val taskReads: Reads[TaskInfra] =
    (
      (__ \ "sid").read[String] and
        (__ \ "info").read[String] and
        (__ \ "data").read[JsValue] and
        (__ \ "task").readNullable[TaskModel]
    )(TaskInfra.apply _)

  implicit val taskWrites: OWrites[TaskInfra] = OWrites[TaskInfra] { task =>
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

  implicit val statusEnum: Enum[TaskStatus] = Enum.derived[TaskStatus]
  implicit val format: Format[TaskStatus] =
    core.JsonImplicits.enumFormat(statusEnum)

  def toString(task: TaskStatus): String =
    task match {
      case Submitted      => "Submitted"
      case Successful     => "Successful"
      case TechnicalError => "TechnicalError"
      case BusinessError  => "BusinessError"
      case Cancelled      => "Cancelled"
      case Warning        => "Warning"
    }

  def fromString(string: String): Option[TaskStatus] =
    statusEnum.values.toSeq.find(order => toString(order) == string)

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

  case object AutoCodeProduitCorrection extends TaskType

  val desktopTasks: Set[TaskType] =
    Set(PrintAuto, PrintUnite, PrintHistory, ImportFile)

  implicit val enumTask: Enum[TaskType] = Enum.derived[TaskType]

  implicit def taskTypeQueryStringBindable(
      implicit stringBinder: QueryStringBindable[String])
    : QueryStringBindable[TaskType] = {
    new QueryStringBindable[TaskType] {
      override def bind(key: String, params: Map[String, Seq[String]])
        : Option[Either[String, TaskType]] = {
        params.get(s"$key") match {
          case Some(Seq(str)) => enumTask.decodeOpt(str).map(Right(_))
          case _              => None
        }
      }

      override def unbind(key: String, value: TaskType): String = {
        stringBinder.unbind(key, enumTask.encode(value))
      }
    }
  }

}
