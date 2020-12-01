package actors

import akka.actor._
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Subscribe, SubscribeAck}

import scala.concurrent.duration._
import play.api.libs.json._
import akka.NotUsed
import akka.stream.{ Materializer, OverflowStrategy}
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source
import akka.stream.CompletionStrategy
import akka.Done

class PageActor(sid: String, out: ActorRef)(implicit system: ActorSystem, mat: Materializer) extends Actor with ActorLogging {

  val topic = s"jobs:${sid}"

  val mediator = DistributedPubSub(system).mediator

  mediator ! Subscribe(topic, self)

   val completeWithDone: PartialFunction[Any, CompletionStrategy] = { case Done => CompletionStrategy.immediately }

  val throttler = Source.actorRef[String](
    completionMatcher = completeWithDone,
    failureMatcher = PartialFunction.empty,
    bufferSize = 100000, 
    OverflowStrategy.dropNew)
    .groupedWithin(100, 3 seconds) 
    .map((infos: Seq[String]) => Json.obj(
      "type" -> "TasksComplete",
      "info" -> infos
      ))
    .to(Sink.actorRef(out, NotUsed,  ex => "FAILED: " + ex.getMessage))  
    .run()

    def receive = {
      case JobManager.TaskComplete(ssid, info) =>
        println(s"task complete: {}, $ssid, $info")

        throttler ! info

        
      case SubscribeAck(Subscribe(`topic`, None, `self`)) ⇒
        log.info("subscribing")
    }
}
