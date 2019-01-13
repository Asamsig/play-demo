package controllers

import com.google.inject.Inject
import play.api.Logger
import play.api.mvc.InjectedController

class PresentationController @Inject()() extends InjectedController {

  val logger = Logger(getClass)

  def index = Action {
    Ok
  }

}
