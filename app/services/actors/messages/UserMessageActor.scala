package services.actors.messages

import akka.actor._
import akka.routing._
import services.actors._


object UserMessageActor {
  case class Connect(socket: ActorRef)
}

abstract class UserMessageActor extends EntityActor {

  var sockets: Router = Router(BroadcastRoutingLogic(), Vector())

  override def isIdle: Boolean = sockets.routees.isEmpty

  override def receive: Receive = ({
    case Envelope(id:String, c: UserMessageActor.Connect) =>
      log.info(s"Socket ${c.socket.path} connected")
      sockets = sockets.addRoutee(c.socket)
      context watch c.socket
      self ! EntityActor.UnsetReceiveTimeout
      log.info("{}, Now has {} sockets connected.", self.path.name, sockets.routees.length)

    case Terminated(a) =>
      log.info("Socket {} Disconnected.", a.path)
      context unwatch a
      sockets = sockets.removeRoutee(a)
      if (isIdle) self ! EntityActor.SetReceiveTimeout
      log.info("{}, Now has {} sockets connected.", self.path.name, sockets.routees.length)
  }: Receive) orElse super.receive
}
