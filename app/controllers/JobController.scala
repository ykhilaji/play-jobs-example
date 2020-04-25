package controllers

import akka.actor._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

import util.AkkaSupport

import javax.inject._
import play.api._
import play.api.mvc._

import services.JobService

@Singleton
class JobController @Inject() (jobService: JobService,
    cc: ControllerComponents) (implicit system: ActorSystem, ec: ExecutionContext) extends AbstractController(cc) {

  def job(sid: String, tasks: Int) = Action { implicit request: Request[AnyContent] =>

    val topic = s"jobs:${sid}"

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
