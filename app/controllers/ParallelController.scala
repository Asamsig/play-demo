package controllers

import java.util.concurrent.Executors

import akka.stream.scaladsl.{Concat, Merge, Source}
import com.google.inject.Inject
import org.apache.commons.lang3.concurrent.BasicThreadFactory
import play.api.Logger
import play.api.libs.concurrent.Futures
import play.api.libs.ws.WSClient
import play.api.mvc.InjectedController

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

class ParallelController @Inject()(ws: WSClient)(implicit futures: Futures)
    extends InjectedController {

  val logger = Logger(getClass)

  implicit val executionContext = ExecutionContext.fromExecutorService(
    Executors.newSingleThreadExecutor(
      new BasicThreadFactory.Builder()
        .namingPattern("primary-thread-%d")
        .build()))

  def withoutComprehension = Action.async {
    implicit val start = System.currentTimeMillis()

    ws.url("https://adressa.no").get().map(latency).flatMap { adressa =>
      ws.url("https://evry.no").get().map(latency).flatMap { evry =>
        ws.url("https://nrk.no").get().map(latency).map { nrk =>
          Ok((adressa + evry + nrk).toString())
        }
      }
    }
  }

  def withoutComprehensionParallel = Action.async {
    implicit val start = System.currentTimeMillis()

    val futureAdressa = ws.url("https://adressa.no").get().map(latency)
    val futureEvry = ws.url("https://evry.no").get().map(latency)
    val futureNrk = ws.url("https://nrk.no").get().map(latency)

    futureAdressa.flatMap { adressa =>
      futureEvry.flatMap { evry =>
        futureNrk.map { nrk =>
          Ok((adressa + evry + nrk).toString())
        }
      }
    }
  }

  def index = Action.async {
    implicit val start = System.currentTimeMillis()

    for {
      adressa <- ws.url("https://adressa.no").get().map(latency)
      evry <- ws.url("https://evry.no").get().map(latency)
      nrk <- ws.url("https://nrk.no").get().map(latency)
    } yield {
      Ok((adressa + evry + nrk).toString())
    }
  }

  def parallel = Action.async {
    implicit val start = System.currentTimeMillis()

    val futureAdressa = ws.url("https://adressa.no").get().map(latency)
    val futureEvry = ws.url("https://evry.no").get().map(latency)
    val futureNrk = ws.url("https://nrk.no").get().map(latency)

    for {
      adressa <- futureAdressa
      evry <- futureEvry
      nrk <- futureNrk
    } yield {
      Ok((adressa + evry + nrk).toString())
    }
  }

  def merge = Action {
    val start = System.currentTimeMillis()

    val eventualLong =
      Source.fromFuture(
        futures.delayed(5 seconds)(timeLoad("https://google.no", start))
      )
    val eventualLong1 =
      Source.fromFuture(
        timeLoad("https://google.dk", start)
      )

    Ok.chunked(Source.combine(eventualLong, eventualLong1)(Merge(_)))
  }

  def concat = Action {
    val start = System.currentTimeMillis()

    val eventualLong =
      Source.fromFuture(
        futures.delayed(5 seconds)(timeLoad("https://google.no", start))
      )
    val eventualLong1 =
      Source.fromFuture(
        timeLoad("https://google.dk", start)
      )

    Ok.chunked(Source.combine(eventualLong, eventualLong1)(Concat(_)))
  }

  private def latency(any: Any)(implicit start: Long) = {
    logger.info("was executed")
    Thread.sleep(3000)
    (System.currentTimeMillis() - start) millis
  }

  private def timeLoad(site: String, start: Long) = {
    ws.url(site)
      .get()
      .map { _ =>
        logger.info("was executed")
        s"$site: ${latency("")(start)}\n"
      }
  }

}
