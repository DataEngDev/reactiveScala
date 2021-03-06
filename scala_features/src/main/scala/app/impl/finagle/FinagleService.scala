package app.impl.finagle

import com.twitter.finagle.{Failure, http, _}
import com.twitter.util.Future

/**
  * Created by pabloperezgarcia on 08/04/2017.
  * The Service class will receive and response a Future[Response] with types that you specify
  * Service[Req,Rep]
  */
object FinagleService {

  var sleepTime = 0
  var responseType = "ok"

  val service = new Service[http.Request, http.Response] {
    def apply(req: http.Request): Future[http.Response] = {
      Thread.sleep(sleepTime)
      responseType match {
        case "ok" => {
          val rep = req.getResponse()
          rep.headerMap.add("Cookie", "New header!")
          Future.value(rep)
        }
        case "error_retry" =>
          responseType = "ok"
          Future.exception(Failure.rejected("busy"))
        case "error_non_retry" => Future.exception(Failure("Don't try again",
          Failure.Rejected | Failure.NonRetryable))
      }
    }
  }
}
