package org.ebayopensource.regression.internal.reportGenerator

import java.io.{BufferedWriter, File, FileWriter}

import org.fusesource.scalate.{TemplateEngine, TemplateSource}

import scala.io.Source
import scala.util.{Failure, Success, Try}

/**
  * Created by asfernando on 4/22/17.
  */
class HTMLReportGenerator extends ReportGenerator {

  val scalateEngine = new TemplateEngine

  def getContent(reportEntries: Seq[ReportEntry]) : Try[String] = Try {

    if (reportEntries.size==0) {
      throw new IllegalArgumentException("Cannot generate report with 0 reportEntries.")
    }

    val templateText = Source.fromInputStream(getClass.getResourceAsStream("/report/index.html")).mkString
    scalateEngine.escapeMarkup = false

    val regressionCount :Seq[Int] = reportEntries.flatMap {
      reportEntry => {
        reportEntry.requestReportEntries.map {
          requestReportEntry => {
            requestReportEntry.reqMessages.size
          }
        }
      }
    }

    val renderedContent = scalateEngine.layout(TemplateSource.fromText("/com/ebay/n/regression/text.ssp", templateText),
      Map("reportEntries" -> reportEntries, "regressionCount" -> regressionCount.sum))
    renderedContent
  }

  def writeAndGetFile(content: String, reportFilePath: String) : Try[File] = Try {
    val outputFile = new File(reportFilePath)
    val bw = new BufferedWriter(new FileWriter(outputFile))
    bw.write(content)
    bw.close()
    outputFile
  }

  override def generate(reportEntries: Seq[ReportEntry], reportFilePath: String): Try[File] = Try {
    getContent(reportEntries).flatMap {
      content => writeAndGetFile(content, reportFilePath)
    } match {
      case Success(file) => file
      case Failure(t) => throw t
    }
  }
}


