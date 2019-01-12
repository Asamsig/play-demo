package services

import com.google.inject.Inject
import models.Person

import scala.concurrent.{ExecutionContext, Future}

class DatabaseAsyncMock @Inject()(databaseSyncMock: DatabaseSyncMock) {

  def fetchPeople()(
      implicit executionContext: ExecutionContext): Future[Seq[Person]] =
    Future {
      databaseSyncMock.fetchPeople()
    }

}
