import com.google.inject.AbstractModule

import services._
import play.api.Configuration
import kamon.Kamon

class Module extends AbstractModule {

  def configure() = {

   Kamon.init()

    bind(classOf[JobService]).toProvider(classOf[JobServiceProvider])


  }

}