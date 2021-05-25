package services.sockets

import akka.Done
import akka.actor._
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.{ActorAttributes, CompletionStrategy, Materializer, OverflowStrategy}
import play.api.libs.json.{JsValue, Json}
import services.actors._
import services.actors.messages._
import services.sockets.NotificationProtocol.{Notify, TaskOut}
import services.sockets.ProtocolV2._

import scala.concurrent.duration._



object UserWebSocket {

  def props(id: String, out: ActorRef)(implicit system: ActorSystem, mat: Materializer): Props = Props(new UserWebSocket(id, out))
}

class UserWebSocket(id: String, out: ActorRef)(implicit system: ActorSystem, mat: Materializer) extends Actor with ActorLogging {

  val notificationActor: ActorRef = NotificationActor.getRegion(context.system)

  notificationActor ! Envelope(id, EntityActor.Initialized)
  notificationActor ! Envelope(id, UserMessageActor.Connect(self))

  override def preStart(): Unit = {
    log.info("UserWebSocket::{} is created", self)
  }

  val completeWithDone: PartialFunction[Any, CompletionStrategy] = { case Done => CompletionStrategy.immediately }

  val throttler = Source.actorRef[Payload](
    completionMatcher = completeWithDone,
    failureMatcher = PartialFunction.empty,
    bufferSize = 100000,
    OverflowStrategy.dropNew)
    .groupedWithin(100, 3 seconds)
    .log(self.path.name)
    .withAttributes(
      ActorAttributes.dispatcher("task-stream-dispatcher"))
    .to(Sink.foreach(out !))
    .run()

  override def receive: Receive = ({
    case inEvent: Payload =>
      log.info("Received client message: {}", inEvent)
      out ! inEvent

    case notify: String =>
      out ! Notify(notify)

    case data: JsValue =>
      log.info("Received client message: {}", data)
      out ! TaskOut(data)
    case a: Any =>
      println("========Notification Unhandled Message=========")
      println(a)
  })

  override def postStop(): Unit = {
    log.info(s"UserWebSocket::{} is removed", self)
  }
}
