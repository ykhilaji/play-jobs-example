package actors
import org.apache.pekko.actor._
import org.apache.pekko.actor.Props
import org.apache.pekko.cluster.pubsub.DistributedPubSub
import org.apache.pekko.cluster.pubsub.DistributedPubSubMediator.{
  Subscribe,
  SubscribeAck
}

import scala.concurrent.duration._

import org.apache.pekko.stream.{Materializer, OverflowStrategy}
import org.apache.pekko.stream.scaladsl.{Sink, Source}
import org.apache.pekko.stream.CompletionStrategy
import org.apache.pekko.Done

import play.api.libs.json._
import org.apache.pekko.stream.ActorAttributes
import org.apache.pekko.stream.Supervision
import protocol._

class PageActor(sid: String, out: ActorRef)(implicit system: ActorSystem)
    extends Actor
    with ActorLogging {
  val topic = s"jobs:${sid}"
  implicit val materializer = Materializer.matFromSystem(system)

  override def preStart(): Unit = {
    val mediator = DistributedPubSub(context.system).mediator

    log.info("Subscribing to {}.", topic)
    mediator ! Subscribe(topic, self)
  }

  override def postStop(): Unit = {
    log.info("Page actor {} stopped.")
    super.postStop()
  }

  /* Restart child actor if an ActorInitializationException (i.e. an exception raised in method preStart)
  or an Exception is raised */
  val decider: Supervision.Decider = {
    case e: ActorInitializationException =>
      log.info("Error during actor initialization", e)
      Supervision.Restart
    case e: ActorKilledException => // It should never happen (ie: ActorKilledException is thrown when an Actor receives the akka.actor.Kill message)
      log.info("Actor got killed", e)
      Supervision.Stop
    case e: DeathPactException => // It should never happen (ie: This exception is thrown when watchers send a terminated message)
      log.info("Actor noticed death pact", e)
      Supervision.Stop
    case e: Exception =>
      log.info("Unexpected exception, restarting", e)
      Supervision.Resume
    case t =>
      log.info("Received throwable", t)
      Supervision.Stop
  }

  val completeWithDone: PartialFunction[Any, CompletionStrategy] = {
    case Done => CompletionStrategy.immediately
  }

  val throttler = Source
    .actorRef[JsValue](completionMatcher = completeWithDone,
                       failureMatcher = PartialFunction.empty,
                       bufferSize = 100000,
                       OverflowStrategy.dropNew)
    .groupedWithin(100, 3 seconds)
    .map(
      (infos: Seq[JsValue]) =>
        Json.obj(
          "type" -> "TasksComplete",
          "info" -> infos
      ))
    .log(self.path.name)
    .withAttributes(ActorAttributes.dispatcher("task-stream-dispatcher") and
      ActorAttributes.supervisionStrategy(decider))
    .to(Sink.foreach(out !))
    .run()

  def receive = {
    case TaskComplete(task) =>
      println(s"task complete----------------: {}, ${task.sid}, ${task.info}")
      out ! Json.toJson(task)

    case SubscribeAck(Subscribe(`topic`, None, `self`)) ⇒
      log.info("subscribing")
  }
}
