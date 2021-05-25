package controllers

import akka.actor.ActorSystem
import play.api.mvc._

import services.actors.messages.NotificationRegionComponents
import play.api.libs.json._
import javax.inject._
import services.JobService

@Singleton
class JobShardingController @Inject()(
  jobService: JobService,
  cc: ControllerComponents)(implicit val actorSystem: ActorSystem ) extends AbstractController(cc) with NotificationRegionComponents {

  def broadcast: Action[JsValue] = Action(parse.json) { implicit req =>
    val allUsers = (req.body \ "userIds").as[Seq[String]]
    allUsers foreach { uid =>

    println(s"------------userIds : ${uid}")
     //_notificationRegion ! Envelope(uid, "blabla")
     jobService.onTest(userId = uid)
    }
    Ok(Json.toJson("message" -> "success"))
  }

 


}