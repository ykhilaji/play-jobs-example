package core

import scala.concurrent.{Future , ExecutionContext}

import play.api.libs.json._

import play.api.Environment
import javax.inject.Inject
import play.Logger

class  Mock @Inject() (env : Environment ) (implicit executionContext : ExecutionContext )  {

  val root = "public/mock/"


  def loadJsonAs[T : Reads](filename: String): Future[T] = {
    val path = root + filename
    val maybeStream = env.resourceAsStream(path)
    val stream = maybeStream.getOrElse(sys.error(s"Impossibilité de charger le fichier $path"))

    val json = Json.parse(stream).as[T]
    
    Future.successful(json)
  }

  def loadJson(filename: String): Future[JsValue] =
    loadJsonAs[JsValue](filename)

}
