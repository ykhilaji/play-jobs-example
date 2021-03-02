package services

import akka.actor._
import javax.inject._

import scala.concurrent.duration._
import akka.NotUsed
import akka.stream.{Materializer, OverflowStrategy, ThrottleMode}
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source
import play.api.Logger
import play.api.inject.ApplicationLifecycle
import akka.cluster.Cluster

import scala.concurrent.Future
import akka.stream.CompletionStrategy
import akka.Done
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Publish
import akka.cluster.pubsub.DistributedPubSubMediator
import actors.PageActor
import play.api.libs.json.Json

import core._
import scala.concurrent.ExecutionContext
import model.TaskModel
import model.TaskInfra

trait JobService {

  def onTask(task: TaskInfra) : Unit
}

@Singleton
class JobServiceDPSImpl @Inject() (lifecycle: ApplicationLifecycle)(implicit system: ActorSystem, mat: Materializer, ex: ExecutionContext) extends JobService  {

  val cluster = Cluster(system)

  cluster.registerOnMemberUp {
    Logger.debug("Member is ready.")
  }

  cluster.registerOnMemberRemoved {
    Logger.debug("Member is down, stopping actor system.")
    system.terminate()
  }

  lifecycle.addStopHook { () =>
    Future.successful(system.terminate())
  }

  val completeWithDone: PartialFunction[Any, CompletionStrategy] = { case Done => CompletionStrategy.immediately }

  val pubsub = DistributedPubSub(system).mediator

  val rateLimiter = Source.actorRef[DistributedPubSubMediator.Publish](
    completionMatcher = completeWithDone,
    failureMatcher = PartialFunction.empty,
    bufferSize = 100000, 
    OverflowStrategy.dropNew)
    .throttle(
      elements = 1000, // 1000 messages per second
      per = 1 second,
      maximumBurst = 10,
      mode = ThrottleMode.Shaping
    )
    .to(Sink.actorRef(pubsub, NotUsed, ex => "FAILED: " + ex.getMessage))
    .run()
    

  def onTask(task: TaskInfra) = {
    import protocol.TaskComplete
    val topic = s"jobs:${task.sid}"
    rateLimiter ! Publish(topic , TaskComplete(task))   
  }

}


@Singleton 
class JobServiceProvider @Inject()(lifecycle: ApplicationLifecycle)(implicit system: ActorSystem,mat: Materializer, ex: ExecutionContext) extends javax.inject.Provider[JobService] {
  lazy val get: JobService =  new JobServiceDPSImpl(lifecycle)
}
