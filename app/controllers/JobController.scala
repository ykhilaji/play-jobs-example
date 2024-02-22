package controllers

import akka.actor._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

import javax.inject._
import play.api.mvc._

import services.JobService
import core.Mock
import model.TaskModel
import model.TaskInfra
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsError
import play.api.libs.json.Json

@Singleton
class JobController @Inject()(mock: Mock,
                              jobService: JobService,
                              components: ControllerComponents)(
    implicit system: ActorSystem,
    ec: ExecutionContext)
    extends AbstractController(components) {

  def job(sid: String, tasks: Int) = Action {
    implicit request: Request[AnyContent] =>
      (1 to tasks) foreach { i =>
        {

          akka.pattern.after(500 millis, using = system.scheduler) {
            val dateFuture = mock.loadJson("task.json")
            for {
              data <- dateFuture
            } yield {
              val task = TaskModel.taskReads.reads(data) match {
                case JsSuccess(value, path) => Some(value)
                case JsError(errors)        => println(errors); None
              }
              val taskInfra =
                TaskInfra(sid, s"task $i complet ", Json.toJson("sid"), task)

              Future.successful {
                jobService.onTask(taskInfra)
              }
            }
          }

        }
      }

      NoContent
  }
}
