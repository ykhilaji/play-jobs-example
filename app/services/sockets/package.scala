import play.api.libs.json._
import play.api.mvc.WebSocket.MessageFlowTransformer

import scala.util._

package object sockets {

  case object JsonParseError extends Exception("json.parse.error")

  def caseClassMessageFlowTransformer[A: Format, B: Format]: MessageFlowTransformer[Try[A], B] = {
    MessageFlowTransformer.jsonMessageFlowTransformer.map(
      in  => Json.fromJson[A](in).fold(_ => Failure(JsonParseError), success => Success(success)),
      out => Json.toJson(out)
    )
  }
}
