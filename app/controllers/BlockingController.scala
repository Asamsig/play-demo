package controllers

import com.google.inject.Inject
import models.Person
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.InjectedController
import services.DatabaseSyncMock

class BlockingController @Inject()(db: DatabaseSyncMock)
    extends InjectedController {

  val logger = Logger(getClass)

  def index() = Action {
    logger.info(s"Start")

    val people: Seq[Person] = db.fetchPeople()

    logger.info(s"fetched people: $people")

    Ok(Json.toJson(people))
  }

}
