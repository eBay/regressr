package org.ebayopensource.regression.internal.http

import scala.util.Try
import scalaj.http.{BaseHttp, HttpOptions}

/**
  * Created by asfernando on 4/17/17.
  */
class SimpleHttpClient(client: BaseHttp) extends BaseHttpClient {

  val timeoutMs = 20000;

  override def execute(testRequest: HTTPRequest): Try[HTTPResponse] = Try {

    var request = client(testRequest.url.toString)
    .headers(testRequest.headers)
    .method(testRequest.method.toString)
    .option(HttpOptions.allowUnsafeSSL)
    testRequest.body.foreach {
      body => {
        request = request.postData(body)
      }
    }
    request = request.timeout(timeoutMs, timeoutMs)
    val httpResponse = request.asString

    HTTPResponse(testRequest,
                 httpResponse.code,
                 Some(httpResponse.body),
                 httpResponse.headers.map {
                   case (k, v) => (k, v(0))
                 })
  }
}
