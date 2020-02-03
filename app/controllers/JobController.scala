package controllers

import akka.actor._
import javax.inject._
import play.api.mvc._
import services.JobService

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

@Singleton
class JobController @Inject() (jobService: JobService,components: ControllerComponents) (implicit system: ActorSystem, ec: ExecutionContext) extends AbstractController(components) {

  def job(sid: String, tasks: Int) = Action { implicit request: Request[AnyContent] =>

    (1 to tasks) foreach {
      i => {

        akka.pattern.after(500 millis, using = system.scheduler) {
          Future.successful(jobService.onTask(sid, s"task $i complete"))
        }

      }
    }

    NoContent
  }
}
