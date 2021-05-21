package services.actors

import akka.actor._
import akka.cluster.sharding._
import akka.util.Timeout

import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * Created by peixiaobin on 2020/11/25.
 */

// Companion to the EntityActor abstract class
object EntityActor {

  case object SetReceiveTimeout
  case object UnsetReceiveTimeout


  trait State
  case object Initializing extends State
  case object Initialized extends State
  case object Missing extends State
  case object Creating extends State
  case object Persisting extends State
  case object FailedToLoad extends State
}

// Base class for the Event Sourced entities to extend from
abstract class EntityActor extends Actor with Stash with ActorLogging {

  import ShardRegion.Passivate

  val id: String              = self.path.name
  val entityType: String      = this.getClass.getSimpleName
  val receiveTimeout: Timeout = 20 minutes

  def isIdle: Boolean = true

  override def preStart(): Unit = {
    super.preStart()
    self ! EntityActor.SetReceiveTimeout
  }

  def receive: Receive = handleTimeout orElse stashAll

  def handleTimeout: Receive = {

    case EntityActor.SetReceiveTimeout =>
      log.info("{} entity with id {} set ReceiveTimeout[{}] and using this scheduled task as the passivation mechanism.", entityType, id, receiveTimeout)
      context.setReceiveTimeout(receiveTimeout.duration)

    case EntityActor.UnsetReceiveTimeout =>
      log.info("{} entity with id {} set no ReceiveTimeout.", entityType, id)
      context.setReceiveTimeout(Duration.Undefined)

    //Have been idle too long, time to start passivation process
    case ReceiveTimeout =>
      log.info("{} entity with id {} is being passivated due to inactivity. Passivated after being idle for {}.", entityType, id, receiveTimeout)
      // context stop self
      context.parent ! Passivate(stopMessage = PoisonPill)
  }

  def stashAll: Receive = {
    case msg => println(msg); println("DOWN IN HERE") ;stash()
  }
}