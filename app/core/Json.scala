package core

import enum.Enum
import org.joda.time.DateTime

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.i18n.{ Lang }
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import java.io.ObjectInputStream


object JsonImplicits {

  implicit val langReads = (
    (__ \ "language").read[String] and
    (__ \ "country").read[String]
  ).tupled.map { case (l, c) => Lang(l, c) }

  implicit val langWrites = OWrites[Lang] { lang =>
    Json.obj("language" -> lang.language, "country" -> lang.country)
  }

  //Pattern used by PostgreSQL for Timestamp with Timezone
  val pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSZZ"
  implicit val dateReads: Reads[DateTime] = JodaReads.jodaDateReads(pattern)

  // Converts a JsString to a value of Enum[A]
  implicit def enumReads[A](implicit enum: Enum[A]): Reads[A] = new Reads[A] {
    val invalidValueError = JsonValidationError(s"Invalid value. It should be one of ")

    def reads(json: JsValue): JsResult[A] = json match {
      case JsString(s) => enum.decodeOpt(s).map(JsSuccess(_))
        .getOrElse(JsError(invalidValueError))

      case _ => JsError(invalidValueError)
    }
  }

  // Converts an Enum[A] to a JsString
  implicit def enumWrites[A](implicit enum: Enum[A]): Writes[A] = Writes[A] { value =>
    JsString(enum.encode(value))
  }

  implicit def enumFormat[A](implicit enum: Enum[A]): Format[A] =
    Format(enumReads, enumWrites)
}

object OptionJsonImplicit {
   implicit def optionFormat[T: Format]: Format[Option[T]] = new Format[Option[T]] {
      override def reads (json: JsValue): JsResult[Option[T]] = json.validateOpt[T]
      
      override def writes (o: Option[T]): JsValue = o match {
         case Some(t) ⇒ implicitly[Writes[T]].writes(t)
         case None ⇒ JsNull
      }
   }
}

object JsonExtensions {
  def withDefault[A](key:String, default:A)(implicit writes:Writes[A]) = __.json.update((__ \ key).json.copyFrom((__ \ key).json.pick orElse Reads.pure(Json.toJson(default))))
}

object Utils {

  // Serialize object to byte array
  def writeToByteArray(obj: Any): Array[Byte] = {
    val baos = new ByteArrayOutputStream
    val oos = new ObjectOutputStream(baos)
    try {
      oos.writeObject(obj)
      baos.toByteArray
    } finally {
      try {
        oos.close
      } catch {
        case _: Throwable => // Do nothing
      }
    }
  }

  // Deserialize object from byte array
  def readFromByteArray[A](bytes: Array[Byte]): A = {
    val bais = new ByteArrayInputStream(bytes)
    val ois = new ObjectInputStream(bais)
    try {
      val obj = ois.readObject
      obj.asInstanceOf[A]
    } finally {
      try {
        ois.close
      } catch {
        case _: Throwable => // Do nothing
      }
    }
  }
}
