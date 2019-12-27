import com.google.inject.AbstractModule

import services.JobService

class Module extends AbstractModule {

  def configure() = {
    bind(classOf[JobService]).asEagerSingleton()

  }

}