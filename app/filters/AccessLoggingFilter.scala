package filters

import akka.stream.Materializer
import javax.inject.Inject
import play.api.Logger
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class AccessLoggingFilter @Inject()(implicit val mat: Materializer,
                                    ec: ExecutionContext)
    extends Filter {

  def apply(nextFilter: RequestHeader => Future[Result])(
      requestHeader: RequestHeader): Future[Result] = {

    val start = System.currentTimeMillis
    Logger.info(
      s"Started processing request ${requestHeader.method} ${requestHeader.uri}")

    nextFilter(requestHeader).map { result =>
      val requestTime = System.currentTimeMillis - start

      Logger.info(
        s"${requestHeader.method} ${requestHeader.uri} took ${requestTime}ms and returned ${result.header.status}")

      result.withHeaders("Request-Time" -> requestTime.toString)
    }
  }
}
