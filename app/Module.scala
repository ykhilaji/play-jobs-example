import org.apache.pekko.actor.typed.{ActorRef, Behavior}
import org.apache.pekko.stream.Materializer
import com.google.inject.AbstractModule
import scala.concurrent.ExecutionContext

import services._
import repository.BordereauModel._

class Module extends AbstractModule {

  override def configure() = {
    bind(classOf[JobService]).toProvider(classOf[JobServiceProvider])
    bind(classOf[BordereauRepository]).to(classOf[BordereauRepositoryImpl])

  }

}
