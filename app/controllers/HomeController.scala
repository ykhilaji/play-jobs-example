package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import scala.concurrent.ExecutionContext
import akka.actor.ActorSystem

import services.actors.messages.NotificationActor
/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(components: ControllerComponents)( implicit val actorSystem: ActorSystem, executionContext: ExecutionContext) extends AbstractController(components){


  NotificationActor.startRegion(actorSystem)


  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>

   println("------------------------------Actor ShardRegions have been started ----------.")

    Ok(views.html.index())
  }
}
