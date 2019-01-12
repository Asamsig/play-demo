package services

import com.google.inject.Inject
import models.Person
import play.api.Configuration

class DatabaseSyncMock @Inject()(config: Configuration) {

  def fetchPeople(): Seq[Person] = {
    Thread.sleep(config.getMillis("database.delay"))

    Seq(
      Person("Alexander Samsig",
             Set("Scala",
                 "Reactive Programming",
                 "Functional Programming",
                 "Functional Reactive Programming")))
  }

}
