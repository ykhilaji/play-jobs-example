package controllers

import javax.inject._
import play.api.mvc._
import scala.concurrent.ExecutionContext
import akka.actor.ActorSystem
import model.TaskType
import model.TaskStatus
import play.api.libs.json._
import core.JsonImplicits.{dateReads, enumReads, enumWrites}

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(components: ControllerComponents)(
    implicit val actorSystem: ActorSystem,
    executionContext: ExecutionContext)
    extends AbstractController(components) {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index() = Action { implicit request: Request[AnyContent] =>
    val json = Json.obj("key" -> TaskStatus.Submitted.toString())
    Ok(views.html.index())
  }
}
