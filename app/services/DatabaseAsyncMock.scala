package services

import java.util.concurrent.Executors

import com.google.inject.Inject
import models.Person
import org.apache.commons.lang3.concurrent.BasicThreadFactory
import play.api.Logger

import scala.concurrent.{ExecutionContext, Future}

class DatabaseAsyncMock @Inject()(databaseSyncMock: DatabaseSyncMock) {

  val logger = Logger(getClass)

  implicit val databaseExecutionContext: ExecutionContext =
    ExecutionContext.fromExecutorService(
      Executors.newSingleThreadExecutor(
        new BasicThreadFactory.Builder()
          .namingPattern("database-thread-%d")
          .build()))

  def fetchPeople(): Future[Seq[Person]] =
    Future {
      logger.info("Awaiting response from data")
      databaseSyncMock.fetchPeople()
    }

}
