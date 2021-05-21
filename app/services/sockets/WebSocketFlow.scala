package services.sockets

import akka.actor._
import akka.event.Logging
import akka.stream._
import akka.stream.scaladsl._
import play.api.Logger

/**
 * Provides a flow that is handled by an actor.
 *
 * Created by peixiaobin on 2020/11/24.
 */
object WebSocketFlow {

  val logger: Logger = play.api.Logger(getClass)

  /**
   * Create a flow that is handled by an actor.
   *
   * Messages can be sent downstream by sending them to the actor passed into the props function.  This actor meets
   * the contract of the actor returned by [[http://doc.akka.io/api/akka/current/index.html#akka.stream.scaladsl.Source$@actorRef[T](bufferSize:Int,overflowStrategy:akka.stream.OverflowStrategy):akka.stream.scaladsl.Source[T,akka.actor.ActorRef] akka.stream.scaladsl.Source.actorRef]].
   *
   * The props function should return the props for an actor to handle the flow. This actor will be created using the
   * passed in [[http://doc.akka.io/api/akka/current/index.html#akka.actor.ActorRefFactory akka.actor.ActorRefFactory]]. Each message received will be sent to the actor - there is no back pressure,
   * if the actor is unable to process the messages, they will queue up in the actors mailbox. The upstream can be
   * cancelled by the actor terminating itself.
   *
   * @param props A function that creates the props for actor to handle the flow.
   * @param bufferSize The maximum number of elements to buffer.
   * @param overflowStrategy The strategy for how to handle a buffer overflow.
   */
  def actorRef[In, Out](
    props: ActorRef => Props,
    name: Option[String] = None,
    bufferSize: Int = 32,
    overflowStrategy: OverflowStrategy = OverflowStrategy.dropNew
  )(implicit system: ActorSystem, mat: Materializer, factory: ActorRefFactory): Flow[In, Out, _] = {

    val logging = Logging(system, "WebSocketFlow")

    val completionMatcher: PartialFunction[Any, CompletionStrategy] = {
      case Status.Success(strategy: CompletionStrategy) => strategy
      case Status.Success(_)                            => CompletionStrategy.draining
      case Status.Success                               => CompletionStrategy.draining
    }
    val failureMatcher: PartialFunction[Any, Throwable] = { case Status.Failure(cause) => cause }

    val (outActor, publisher) = Source
      .actorRef[Out](completionMatcher, failureMatcher, bufferSize, overflowStrategy)
      .log("WebSocketActorFlow")(logging)
      .toMat(Sink.asPublisher(false))(Keep.both)
      .run()

    logger.info("-=-=-=Create new channel by WebSocketFlow=-=-=-")
    val channel =
      if (name.isEmpty) factory.actorOf(Props(channelActor(props, outActor)))
      else factory.actorOf(Props(channelActor(props, outActor)), name.get)
    logger.info(s"WebSocket Channel :: $channel")
    val sink = Sink.actorRef(channel, Status.Success(()), (t: Throwable) => Status.Failure(t))
    logger.info("-=-=-=End create channel by WebSocketFlow=-=-=-")
    Flow.fromSinkAndSource(sink, Source.fromPublisher(publisher))
  }

  /**
   * Create a  warp ws connect channel that is handled by an actor.
   *
   * @param props A function that creates the props for actor to handle the flow.
   * @param out The ws out channel.
   */
  private def channelActor(props: ActorRef => Props, out: ActorRef): Actor = new Actor {
    val wsFlowActor: ActorRef = context.watch(context.actorOf(props(out), "wsFlowActor"))

    def receive: Receive = {
      case Status.Success(_) | Status.Failure(_) => logger.info("--PoisonPill--" + sender());wsFlowActor ! PoisonPill
      case Terminated(_)                         => logger.info("--Terminated--" + sender());context.stop(self)
      case util.Success(value)                   => logger.info("--Forward received message--"); wsFlowActor ! value
      case value                                 => println(value) ; out ! NotificationProtocol.Notify("PONG")
    }

    override def supervisorStrategy: OneForOneStrategy = OneForOneStrategy() {
      case _ => SupervisorStrategy.Stop
    }
  }
}
