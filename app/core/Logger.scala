package core

import core.tracer.{PlayRequestId, PlayRequestTracerLoggerFactory}

/* Use as a mixin to get a properly named Logger for your class */
trait Logger {

  implicit val playRequestId = PlayRequestId.random //new PlayRequestId(self =  "")
  protected val LOG = PlayRequestTracerLoggerFactory.getLogger(this.getClass)

}

trait BasicLogger {
  protected val LOG = play.api.Logger(this.getClass)
}
