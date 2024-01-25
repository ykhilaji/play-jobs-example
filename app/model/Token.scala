package models

import play.api.libs.json._
import core.tag._

trait TokenTag

package object token {
  /* A tagged String representing the sessionId passed by La Poste */
  type Token = String @@ TokenTag

  def Token(str: String): Token = tag[TokenTag](str)

  implicit def tokenReads: Reads[Token] = new Reads[Token] {
    def reads(json: JsValue): JsResult[token.Token] = json match {
      case JsString(s) => JsSuccess(token.Token(s))
      case _           => JsError(JsonValidationError("Bad token type."))
    }
  }
}
