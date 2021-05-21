package services.sockets

import akka.actor._
import services.actors._
import services.actors.messages._
import services.sockets.ProtocolV2._



object UserWebSocket {

  def props(id: String, out: ActorRef): Props = Props(new UserWebSocket(id, out))
}

class UserWebSocket(id: String, out: ActorRef) extends Actor with ActorLogging {

  val notificationActor: ActorRef = NotificationActor.getRegion(context.system)

  notificationActor ! Envelope(id, EntityActor.Initialized)
  notificationActor ! Envelope(id, UserMessageActor.Connect(self))

  override def preStart(): Unit = {
    log.info("UserWebSocket::{} is created", self)
  }

  override def receive: Receive = ({
    case inEvent: Payload =>
      log.info("Received client message: {}", inEvent)
      out ! inEvent
  }:Receive) orElse NotificationProtocol.receive(out)

  override def postStop(): Unit = {
    log.info(s"UserWebSocket::{} is removed", self)
  }
}
