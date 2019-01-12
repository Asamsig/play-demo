package controllers

import com.google.inject.Inject
import models.Person
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.InjectedController
import services.DatabaseAsyncMock

import scala.concurrent.ExecutionContext

class AsyncController @Inject()(db: DatabaseAsyncMock)(
    implicit executionContext: ExecutionContext)
    extends InjectedController {

  val logger = Logger(getClass)

  def index() = Action.async {
    logger.info("Start")

    val futurePeople = db.fetchPeople().map { people: Seq[Person] =>
      logger.info(s"fetched people: $people")

      Ok(Json.toJson(people))
    }

    logger.info(s"After fetch was called")

    futurePeople
  }

}
