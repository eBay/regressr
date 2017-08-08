package org.ebayopensource.regression.internal.workflow

import java.net.URI

import org.ebayopensource.regression.internal.http.{BaseHttpClient, HTTPRequest, HTTPResponse}
import org.ebayopensource.regression.internal.reader.{RequestEntry, TestStrategy}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

/**
  * Created by asfernando on 4/29/17.
  */
object WorkflowTools {

  def performRequest(testIdentifier: String, strategy: TestStrategy, request: RequestEntry, httpClient: BaseHttpClient): Try[HTTPResponse] = Try {
    // scalastyle:off
    val tryHTTPResponse = for {
      httpRequest <- convertRequestEntryToRequest(request, strategy)
      httpResponse <- httpClient.execute(httpRequest)
    } yield (httpResponse)
    // scalastyle:on

    tryHTTPResponse match {
      case Success(r) => r
      case Failure(t) => throw t
    }
  }

  def convertRequestEntryToRequest(request: RequestEntry, strategy: TestStrategy): Try[HTTPRequest] = Try {
    val body = if (request.requestBuilder.isEmpty) None else Some(request.requestBuilder.get.buildRequest(request.dataInput).get)
    HTTPRequest(new URI(strategy.service.baseURL.toString + request.path), strategy.headers ++ request.extraHeaders, request.method, body)
  }

  // scalastyle:off
  def performContinuations(testIdentifier: String, testStrategy: TestStrategy, requestEntry: RequestEntry, httpClient: BaseHttpClient): Try[Seq[HTTPResponse]] = Try {
    val queue = mutable.Queue[HTTPRequest]()
    val httpResponses = ListBuffer[HTTPResponse]()

    val firstRequest = convertRequestEntryToRequest(requestEntry, testStrategy)
    if (firstRequest.isFailure) throw firstRequest.failed.get

    queue.enqueue(firstRequest.get)

    while (!queue.isEmpty) {
      httpClient.execute(queue.dequeue()) match {
        case Success(httpResponse) => {
          if (requestEntry.progressPrinter.isDefined) requestEntry.progressPrinter.get.printProgress(httpResponse)
          httpResponses += httpResponse
          val continuationRequests = requestEntry.continuation.get.getContinuations(httpResponse)
          continuationRequests match {
            case Success(requests) => {
              requests.foreach(request => {
                queue.enqueue(request)
              })
            }
            case Failure(t) => {
              throw t
            }
          }
        }
        case Failure(t) => {
          throw t
        }
      }
    }
    httpResponses
  }
  // scalastyle:on

}
