package protocol

import model.TaskInfra

sealed trait Msg extends core.ActorProtocol

final case class TaskComplete(task: TaskInfra) extends Msg
final case class TaskOut(task: TaskInfra) extends Msg
