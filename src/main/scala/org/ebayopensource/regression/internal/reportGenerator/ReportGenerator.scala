package org.ebayopensource.regression.internal.reportGenerator

import java.io.File

import org.ebayopensource.regression.internal.components.comparator.common.{CompareMessage, ExceptionMessage}
import org.ebayopensource.regression.internal.components.recorder.common.RequestRecordingEntry

import scala.util.Try

/**
  * Created by asfernando on 4/22/17.
  */
trait ReportGenerator {

  def generate(reportEntries: Seq[ReportEntry], reportFilePath: String): Try[File]

}

case class ReportEntry(testIdentifier: String, strategyFileContent: String, requestReportEntries: Seq[RequestReportEntry])

case class RequestReportEntry(requestName: String, reqMessages : Seq[CompareMessage],
                              exception: Option[ExceptionMessage], requestRecordedEntry: RequestRecordingEntry, requestReplayedEntry: RequestRecordingEntry)
