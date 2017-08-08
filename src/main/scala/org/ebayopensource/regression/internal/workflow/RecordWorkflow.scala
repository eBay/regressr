package org.ebayopensource.regression.internal.workflow

import org.ebayopensource.regression.internal.datastore.BaseDataStore
import org.ebayopensource.regression.internal.http.{BaseHttpClient, HTTPResponse}
import org.ebayopensource.regression.internal.reader.{RequestEntry, TestStrategy}
import org.ebayopensource.regression.testrun.TestRunException
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success, Try}
/**
  * Created by asfernando on 4/18/17.
  */
class RecordWorkflow(dataStore: BaseDataStore, httpClient: BaseHttpClient) {

  val logger = LoggerFactory.getLogger(classOf[RecordWorkflow])

  def recordState(testIdentifier: String, strategy: TestStrategy): Try[Unit] = Try {
    strategy.requests.foreach {
      request => {
        logger.info(s"Started request ${request.requestName}")
        if (request.continuation.isDefined) {
          WorkflowTools.performContinuations(testIdentifier, strategy, request, httpClient).flatMap {
            httpResponses => recordResponse(testIdentifier, request, httpResponses, dataStore)
          } match {
            case Success(message) => message
            case Failure(exception) => throw exception
          }
        }
        else {
          WorkflowTools.performRequest(testIdentifier, strategy, request, httpClient).flatMap {
            httpResponse => recordResponse(testIdentifier, request, Seq(httpResponse), dataStore)
          } match {
            case Success(message) => message
            case Failure(exception) => throw exception
          }
        }
      }
    }
  }

  def recordResponse(testIdentifier: String, request: RequestEntry, httpResponses: Seq[HTTPResponse], dataStore: BaseDataStore): Try[String] = Try {
    (for {
      recordingEntries <- request.recorder.recordAndFilter(httpResponses)
      identifier <- dataStore.storeRequestRecording(testIdentifier, request.requestName, recordingEntries)
      awesome <- Try(s"Completed ${request.requestName} in test ${identifier} successfully")
    } yield awesome).recover {
      case e => throw new TestRunException(s"Could not record test: ${testIdentifier}. Failed in request: ${request.requestName}. Reason:= ${e}")
    } match {
      case Success(str) => str
      case Failure(t) => throw t
    }
  }
}
