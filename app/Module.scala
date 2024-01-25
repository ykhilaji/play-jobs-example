import com.google.inject.AbstractModule

import services._
import play.api.Configuration
import kamon.Kamon
import repository.BordereauModel._

class Module extends AbstractModule {

  override def configure() = {
    bind(classOf[JobService]).toProvider(classOf[JobServiceProvider])
    bind(classOf[BordereauRepository]).to(classOf[BordereauRepositoryImpl])

  }

}
