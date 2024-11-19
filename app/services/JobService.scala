package services

import org.apache.pekko.actor._
import javax.inject._

import scala.concurrent.duration._
import org.apache.pekko.NotUsed
import org.apache.pekko.stream.{Materializer, OverflowStrategy, ThrottleMode}
import org.apache.pekko.stream.scaladsl.{Sink, Source}
import play.api.Logger
import play.api.inject.ApplicationLifecycle
import org.apache.pekko.cluster.Cluster

import scala.concurrent.Future
import org.apache.pekko.stream.CompletionStrategy
import org.apache.pekko.Done
import org.apache.pekko.cluster.pubsub.DistributedPubSub
import org.apache.pekko.cluster.pubsub.DistributedPubSubMediator.{
  Publish,
  Subscribe
}

import scala.concurrent.ExecutionContext
import model.TaskInfra
import core.BasicLogger
import org.apache.pekko.cluster.pubsub.DistributedPubSubMediator

trait JobService {

  def onTask(task: TaskInfra): Unit
}

@Singleton
class JobServiceDPSImpl @Inject()(lifecycle: ApplicationLifecycle)(
    implicit mat: Materializer,
    system: ActorSystem,
    ex: ExecutionContext)
    extends JobService
    with BasicLogger {

  val cluster = Cluster(system)

  cluster.registerOnMemberUp {
    LOG.debug("Member is ready.")
  }

  cluster.registerOnMemberRemoved {
    LOG.debug("Member is down, stopping actor system.")
    system.terminate()
  }

  lifecycle.addStopHook { () =>
    Future.successful(system.terminate())
  }

  val completeWithDone: PartialFunction[Any, CompletionStrategy] = {
    case Done => CompletionStrategy.immediately
  }

  val pubsub = DistributedPubSub(system).mediator

  val rateLimiter = Source
    .actorRef[DistributedPubSubMediator.Publish](completionMatcher =
                                                   completeWithDone,
                                                 failureMatcher =
                                                   PartialFunction.empty,
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
    rateLimiter ! Publish(topic, TaskComplete(task))
  }

}
@Singleton
class JobServiceProvider @Inject()(lifecycle: ApplicationLifecycle)(
    implicit system: ActorSystem,
    mat: Materializer,
    ex: ExecutionContext)
    extends javax.inject.Provider[JobService] {
  lazy val get: JobService = new JobServiceDPSImpl(lifecycle)
}
