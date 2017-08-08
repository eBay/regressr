package org.ebayopensource.regression.internal.workflow

import java.io.File

import org.ebayopensource.regression.internal.components.comparator.common.{CompareMessage, ExceptionMessage}
import org.ebayopensource.regression.internal.components.recorder.common.{RecordingEntry, RequestRecordingEntry}
import org.ebayopensource.regression.internal.datastore.BaseDataStore
import org.ebayopensource.regression.internal.http.{BaseHttpClient, HTTPResponse}
import org.ebayopensource.regression.internal.reader.{RequestEntry, YAMLTestStrategyReader}
import org.ebayopensource.regression.internal.reportGenerator.{HTMLReportGenerator, ReportEntry, ReportGenerator, RequestReportEntry}
import org.ebayopensource.regression.testrun.TestRunException
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success, Try}

/**
  * Created by asfernando on 4/18/17.
  */
class ReplayWorkflow(dataStore: BaseDataStore, httpClient: BaseHttpClient, reportGenerator: ReportGenerator) {

  val logger = LoggerFactory.getLogger(classOf[ReplayWorkflow])

  def replayAndFetchReport(testIdentifiers: Array[String]): ReplayOutput = {
    val reportEntries: Seq[ReportEntry] = testIdentifiers.map {
      case identifier => {
        replayNRegress(identifier) match {
          case Success(reportEntry) => {
            reportEntry
          }
          case Failure(t) => {
            ReportEntry(identifier, "", Seq(RequestReportEntry("Invalid", Seq(),
              Some(ExceptionMessage(t.getMessage)), RequestRecordingEntry(Seq()), RequestRecordingEntry(Seq()))))
          }
        }
      }
    }

    val regressionCount : Int = reportEntries.map {
      reportEntry => {
        reportEntry.requestReportEntries.map {
          entry => entry.reqMessages.seq.size
        }.sum
      }
    }.sum

    reportGenerator.generate(reportEntries, s"testrun_${System.currentTimeMillis}.html") match {
      case Success(fileName) => {
        ReplayOutput("Report was generated at " + new File(".").getCanonicalPath + "/" + fileName.toString, regressionCount)
      }
      case Failure(t) => {
        ReplayOutput(s"Report could not be generated. Reason:= ${t.getMessage}", regressionCount)
      }
    }
  }

  // scalastyle:off
  def replayNRegress(testIdentifier: String): Try[ReportEntry] = Try {
    val strategyFile = dataStore.getStrategy(testIdentifier)
    if (strategyFile.isEmpty) {
      throw new IllegalArgumentException(s"Invalid strategy ${testIdentifier}. Not present in datastore")
    }
    YAMLTestStrategyReader.read(strategyFile.get) match {
      case Success(strategy) => {
        val requests = strategy.requests
        val requestReportEntries = requests.map {
          request => {
            logger.info(s"Starting request ${request.requestName}")
            if (request.continuation.isDefined) {
              WorkflowTools.performContinuations(testIdentifier, strategy, request, httpClient).flatMap {
                httpResponses =>
                  getComparisons(testIdentifier, dataStore, request, httpResponses).flatMap {
                    compareMessages => {
                      Try(RequestReportEntry(request.requestName, compareMessages._1, None, compareMessages._2, compareMessages._3))
                    }
                  }
              }
            }
            else {
              WorkflowTools.performRequest(testIdentifier, strategy, request, httpClient).flatMap {
                httpResponse => getComparisons(testIdentifier, dataStore, request, Seq(httpResponse)).flatMap {
                  compareMessages => {
                    Try( RequestReportEntry(request.requestName, compareMessages._1, None, compareMessages._2, compareMessages._3) )
                  }
                }
              }
            }
          }
        }.map {
          requestReportEntry => {
            requestReportEntry match {
              case Success(entry) => entry
              case Failure(t) => throw t
            }
          }
        }

        ReportEntry(testIdentifier, strategyFile.get, requestReportEntries)
      }
      case Failure(t) => {
        throw new IllegalStateException("Could not read strategy file:= " + t)
      }
    }
  }
  // scalastyle:on

  def getComparisons(testIdentifier: String, dataStore: BaseDataStore,
                     request: RequestEntry, httpResponses: Seq[HTTPResponse]): Try[(Seq[CompareMessage], RequestRecordingEntry, RequestRecordingEntry)] = Try {
    (for {
      recordedEntry <- dataStore.getRequestRecording(testIdentifier, request.requestName)
      replayedEntry <- request.recorder.recordAndFilter(httpResponses)
      comparatorMessages <- request.comparator.compare(recordedEntry, replayedEntry)
    } yield {
      (comparatorMessages, recordedEntry, replayedEntry)
    }) match {
      case Success(compMessages) => compMessages
      case Failure(t) => {
        throw new TestRunException(s"Could not replay test: ${testIdentifier}. Failed in request: ${request.requestName}. Reason:= ${t.getMessage}")
      }
    }
  }
}

case class ReplayOutput(message: String, noOfRegressions: Int)