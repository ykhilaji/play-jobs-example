package core.sql

import anorm._
import org.postgresql.util.PGobject

import play.api.libs.json._

/* Anorm PG extensions */

object JsValue {

  /* Enables the sending of a JsValue to Anorm/JDBC */
  implicit def jsValueToStatement = new ToStatement[JsValue] {
    def set(s: java.sql.PreparedStatement,
            index: Int,
            aValue: JsValue): Unit = {
      val pgObject = new PGobject()
      pgObject.setType("jsonb")
      pgObject.setValue(Json.toJson(aValue).toString)
      s.setObject(index, pgObject)
    }
  }

  /* Converts an Anorm Column to a JsValue */
  implicit def rowToJsValue: Column[JsValue] = {
    Column.nonNull[JsValue] { (value, meta) =>
      value match {
        case v: org.postgresql.util.PGobject =>
          Right(Json.parse(v.getValue))
        case _ =>
          Left(TypeDoesNotMatch(
            s"Cannot convert $value:${value.asInstanceOf[AnyRef].getClass} to JsValue for column ${meta.column}"))
      }
    }
  }

  implicit object JsValueParameterMetaData extends ParameterMetaData[JsValue] {
    val sqlType = "JSONB"
    val jdbcType = java.sql.Types.OTHER
  }

}

object Enum {
  import anorm.ToStatement.stringToStatement
  import enum.Enum

  /* Enables the sending of a value of enum A to Anorm/JDBC */
  implicit def enumToStatement[A](implicit enum: Enum[A]) = new ToStatement[A] {
    def set(s: java.sql.PreparedStatement, index: Int, aValue: A) = {
      stringToStatement.set(s, index, enum.encode(aValue))
    }
  }

  /* Converts an Anorm Column to a value of enum A */
  implicit def rowToEnum[A](implicit enum: Enum[A]): Column[A] =
    Column.nonNull[A] { (value, meta) =>
      value match {
        case s: String =>
          enum
            .decodeOpt(s)
            .map(Right(_))
            .getOrElse(Left(TypeDoesNotMatch(
              s"Cannot convert $value to ${enum.values} for column ${meta.column}")))

        case _ =>
          Left(TypeDoesNotMatch(
            s"Cannot convert $value to ${enum.values} for column ${meta.column}"))
      }
    }
}
