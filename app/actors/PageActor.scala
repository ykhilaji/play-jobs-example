package actors

import akka.actor._


import scala.concurrent.duration._
import akka.NotUsed
import org.joda.time.{ DateTime }

import akka.stream.{ Materializer, OverflowStrategy}
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source
import akka.stream.CompletionStrategy
import akka.Done


import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.json.Reads._
import akka.stream.ActorAttributes
import akka.stream.Supervision
import model.TaskModel
import model.TaskInfra
import protocol._
import com.sandinh.paho.akka.MqttPubSub
import com.sandinh.paho.akka.PSConfig
import play.api.Logger
import com.sandinh.paho.akka.Subscribe
import com.sandinh.paho.akka.SubscribeAck
import com.sandinh.paho.akka.Message


class PageActor(sid: String, out: ActorRef)(implicit system: ActorSystem, mat: Materializer) extends Actor with ActorLogging {
  import protocol.Messages._
  val topic = s"jobs:${sid}"

  override def preStart(): Unit = {
    
  //val mediator = DistributedPubSub(context.system).mediator

   val mediator = system.actorOf(Props(classOf[MqttPubSub], PSConfig(
    brokerUrl = "tcp://test.mosquitto.org:1883", //all params is optional except brokerUrl
   // userName = null,
   // password = null,
    //messages received when disconnected will be stash. Messages isOverdue after stashTimeToLive will be discard
    stashTimeToLive = 1.minute,
    stashCapacity = 8000, //stash messages will be drop first haft elems when reach this size
    reconnectDelayMin = 10.millis, //for fine tuning re-connection logic
    reconnectDelayMax = 30.seconds
  )))

    Logger.logger.info("Subscribing to {}.", topic)
    mediator ! Subscribe(topic, self)
  }

  override def postStop(): Unit = {
    Logger.logger.info("Page actor {} stopped.")
    super.postStop()
  }

  /* Restart child actor if an ActorInitializationException (i.e. an exception raised in method preStart)
  or an Exception is raised */
  val decider: Supervision.Decider = {
    case e: ActorInitializationException =>
      Logger.logger.info("Error during actor initialization", e)
      Supervision.Restart
    case e: ActorKilledException => // It should never happen (ie: ActorKilledException is thrown when an Actor receives the akka.actor.Kill message)
      Logger.logger.info("Actor got killed", e)
      Supervision.Stop
    case e: DeathPactException => // It should never happen (ie: This exception is thrown when watchers send a terminated message)
      Logger.logger.info("Actor noticed death pact", e)
      Supervision.Stop
    case e: Exception =>
      Logger.logger.info("Unexpected exception, restarting", e)
      Supervision.Resume
    case t =>
      Logger.logger.info("Received throwable", t)
      Supervision.Stop
  }

  val completeWithDone: PartialFunction[Any, CompletionStrategy] = { case Done => CompletionStrategy.immediately }

  val throttler = Source.actorRef[JsValue](
    completionMatcher = completeWithDone,
    failureMatcher = PartialFunction.empty,
    bufferSize = 100000, 
    OverflowStrategy.dropNew)
    .groupedWithin(100, 3 seconds) 
    .map((infos: Seq[JsValue]) => Json.obj(
      "type" -> "TasksComplete",
      "info" -> infos
      ))
    .log(self.path.name)
     .withAttributes(
      ActorAttributes.dispatcher("task-stream-dispatcher") and 
      ActorAttributes.supervisionStrategy(decider))
    .to(Sink.foreach(out !))
    .run()


 override def receive = {
    
    case TaskComplete(task) =>
      Logger.logger.info(s"task complete----------------: {}, ${task.sid}, ${task.info}")
      out ! Json.toJson(task)

    case SubscribeAck(Subscribe(`topic`, `self`, _), fail) =>
      if (fail.isEmpty) context become ready
      else println(fail.get, s"Can't subscribe to $topic")
}

def ready : Receive = {
  case msg: Message => 
    val work = core.Utils.readFromByteArray[TaskComplete](msg.payload)
     Logger.logger.info(s"task complete----------------: {}, ${work.task.sid}, ${work.task.info}")
    throttler ! Json.toJson( work.task)
}

}

