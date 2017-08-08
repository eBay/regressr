package org.ebayopensource.regression.internal.reportGenerator

import java.io.File

import org.ebayopensource.regression.UnitSpec
import org.ebayopensource.regression.internal.components.comparator.common.CompareMessage
import org.ebayopensource.regression.internal.components.recorder.common.{RecordingEntry, RequestRecordingEntry}

/**
  * Created by asfernando on 5/15/17.
  */
class HTMLReportGeneratorTest extends UnitSpec {

  "A HTML Report Generator" should "throw an IllegalArgumentException when an empty input was passed" in {
    assert(new HTMLReportGenerator().getContent(Seq()).isFailure)
    assert(new HTMLReportGenerator().getContent(Seq()).failed.get.isInstanceOf[IllegalArgumentException])
    val entry = ReportEntry("", "", Seq())
    val reqRecordedEntry : Seq[RecordingEntry] = entry.requestReportEntries.flatMap {
      e => e.requestRecordedEntry.entries
    }
  }

  "A HTML Report Generator" should "generate a rendered template when reportEntries are passed" in {
      val reportEntry = ReportEntry( "test1", "this is a strategy file",
        Seq(RequestReportEntry("request1", Seq(CompareMessage("message1", "from", "to")),
          None, RequestRecordingEntry(Seq()), RequestRecordingEntry(Seq()))) )
      val content = new HTMLReportGenerator().getContent(Seq(reportEntry))
      assert(content.isSuccess)
      assert(content.get.length > 0)
  }

  "A HTML Report Generator" should "generate a file report" in {
    val reportEntry = ReportEntry( "test1", "this is a strategy file",
      Seq(RequestReportEntry("request1", Seq(CompareMessage("message1", "from", "to")),
        None, RequestRecordingEntry(Seq()), RequestRecordingEntry(Seq()))) )
    val content = new HTMLReportGenerator().generate(Seq(reportEntry), "/tmp/regressr_temp_report.html")
    assert(content.isSuccess)
    assert(content.get.length > 0)
    new File("/tmp/regressr_temp_report.html").delete()
  }
}
