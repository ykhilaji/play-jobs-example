package actors

import akka.actor._
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Publish


import scala.concurrent.duration._

class JobManager extends Actor with ActorLogging {
  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
    case _: Exception => SupervisorStrategy.Restart
  }

  val mediator = DistributedPubSub(context.system).mediator

  def receive = {
    // case JobManager.JobComplete(sid) =>
    //   mediator ! Publish(topic, JobManager.JobComplete(sid))

    case JobManager.TaskComplete(sid, info) =>
      mediator ! Publish(s"jobs:${sid}", JobManager.TaskComplete(sid, info))
  }
}

object JobManager {
  sealed trait Msg

  case class TaskComplete(sid: String, info: String) extends Msg
  // case class JobComplete(sid: String)
}

