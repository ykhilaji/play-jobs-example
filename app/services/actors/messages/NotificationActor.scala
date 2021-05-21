package services.actors.messages

import akka.actor._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json.JsValue
import services.actors._


object NotificationActor extends AkkaClusterSharding {

  val shardName: String = "notification_actors"

  override def props: Props = Props(new NotificationActor())
}

class NotificationActor extends UserMessageActor {

  println(self.path)
  override def receive: Receive = ({

    case Envelope(_, notify: String) =>
      sockets.route(notify, sender())

    case Envelope(userId, data: JsValue) =>
      println(s"---------send to ${userId}-----------")
      sockets.route(data,sender())

  }: Receive) orElse super.receive

  override def postStop(): Unit = println(s"${DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss").print(DateTime.now)} PRINTLN [${self.path}]is stoped")
}

trait NotificationRegionComponents {
  // self: AkkaTimeOutConfig =>

  def actorSystem: ActorSystem

  def _notificationRegion: ActorRef = NotificationActor.getRegion(actorSystem)
}