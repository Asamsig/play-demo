package controllers

import com.google.inject.Inject
import play.api.libs.ws.WSClient
import play.api.mvc.InjectedController

import scala.concurrent.ExecutionContext

class ProxyController @Inject()(ws: WSClient)(
    implicit executionContext: ExecutionContext)
    extends InjectedController {

  def index = Action.async {
    val responseFuture = ws.url("http://example.com").get()

    val resultFuture = responseFuture.map { resp =>
      Status(resp.status)(resp.body).as(resp.contentType)
    }

    resultFuture
  }

}
