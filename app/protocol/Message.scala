package protocol

import model.TaskInfra

import play.api.libs.json.JsValue
import com.esotericsoftware.kryo.Serializer
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Output
import play.api.libs.json.Json
import com.esotericsoftware.kryo.io.Input
import play.api.libs.json._

abstract class PlayJsonSerializer[T <: JsValue] extends Serializer[T] {
  override def write(kryo: Kryo, output: Output, `object`: T): Unit = output.writeString(Json.stringify(`object`))

  override def read(kryo: Kryo, input: Input, `type`: Class[T]): T = Json.parse(input.readString()).asInstanceOf[T]
}

class JsValueSerializer extends PlayJsonSerializer[JsValue]
class JsObjectSerializer extends PlayJsonSerializer[JsObject]
class JsStringSerializer extends PlayJsonSerializer[JsString]
class JsNumberSerializer extends PlayJsonSerializer[JsNumber]
class JsBooleanSerializer extends PlayJsonSerializer[JsBoolean]
class JsArraySerializer extends PlayJsonSerializer[JsArray]
class JsNullSerializer extends PlayJsonSerializer[JsNull.type]


sealed trait Msg extends core.ActorProtocol

final case class TaskComplete(task: TaskInfra) extends Msg
final case class TaskOut(task: TaskInfra) extends Msg
  