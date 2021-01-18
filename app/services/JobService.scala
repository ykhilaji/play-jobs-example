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
import actors.PageActor
import play.api.libs.json.Json

import core._
import scala.concurrent.ExecutionContext
import model.TaskModel
import model.TaskInfra
import redis.RedisClient
import com.sandinh.paho.akka.MqttPubSub
import com.sandinh.paho.akka.PSConfig
import com.sandinh.paho.akka.Publish

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

  //val pubsub = DistributedPubSub(system).mediator

  val pubsub = system.actorOf(Props(classOf[MqttPubSub], PSConfig(
    brokerUrl = "tcp://test.mosquitto.org:1883", //all params is optional except brokerUrl
   // userName = null,
   // password = null,
    //messages received when disconnected will be stash. Messages isOverdue after stashTimeToLive will be discard
    stashTimeToLive = 1.minute,
    stashCapacity = 8000, //stash messages will be drop first haft elems when reach this size
    reconnectDelayMin = 10.millis, //for fine tuning re-connection logic
    reconnectDelayMax = 30.seconds
  )))


  val rateLimiter = Source.actorRef[Publish](
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
    import protocol.Messages.TaskComplete
    Logger.logger.info(s"task id ======== ${task.task.map(_.id)}")
    val topic = s"jobs:${task.sid}"
  
    val payload = core.Utils.writeToByteArray(TaskComplete(task))

    rateLimiter ! Publish(topic , payload)   
  }

}

@Singleton
class JobServiceRedisImpl @Inject() (lifecycle: ApplicationLifecycle)(implicit system: ActorSystem) extends JobService  {
 
  val redis = RedisClient()
    
  def onTask(task: TaskInfra) = {
     
    val channel = task.sid
 
    redis.publish(channel,task.info)
  }
  

}

@Singleton 
class JobServiceProvider @Inject()(lifecycle: ApplicationLifecycle, conf: Conf)(implicit system: ActorSystem,mat: Materializer, ex: ExecutionContext) extends javax.inject.Provider[JobService] {
  lazy val get: JobService = if (conf.websocket.redisEnabled) new JobServiceRedisImpl(lifecycle) else new JobServiceDPSImpl(lifecycle)
}
