package protocol

import model.TaskInfra
import play.api.libs.json._
import model.TaskModel

sealed trait Msg extends core.ActorProtocol

final case class TaskComplete(task: TaskInfra) extends Msg
final case class TaskOut(task: TaskInfra) extends Msg
