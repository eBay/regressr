package org.ebayopensource.regression.internal.components.recorder

import org.ebayopensource.regression.internal.common.Util
import org.ebayopensource.regression.internal.components.recorder.common.{Recorder, RecordingEntry, RequestRecordingEntry, RequestRecordingEntryTypes}
import org.ebayopensource.regression.internal.http.HTTPResponse

import scala.util.{Failure, Success, Try}

/**
  * Created by asfernando on 4/18/17.
  */
class SimpleHTTPJSONRecorder extends Recorder {

  val mapper = Util.getMapper()

  override def record(responses: Seq[HTTPResponse]): Try[RequestRecordingEntry] = Try {

    val recordingEntries : Seq[RecordingEntry] = responses.flatMap {
      response => {
        getRecordingEntry(response) match {
          case Success(entries) => entries
          case Failure(t) => throw t
        }
      }
    }

    RequestRecordingEntry(recordingEntries)
  }

  def getRecordingEntry(resp: HTTPResponse): Try[Seq[RecordingEntry]] = Try {
    val body = resp.body
    var entries = Seq[RecordingEntry]()
    entries = entries :+ RecordingEntry(RequestRecordingEntryTypes.STRING, resp.status.toString, "response status code")
    entries = entries :+ RecordingEntry(RequestRecordingEntryTypes.STRING, resp.request.url.toString, "request url")
    (resp.request.body.isDefined) match {
      case true => entries = entries :+ RecordingEntry(RequestRecordingEntryTypes.JSON, mapper.writeValueAsString(resp.request.body.get), "request body")
      case _ =>
    }
    entries = entries :+ RecordingEntry(RequestRecordingEntryTypes.STRING, resp.request.method.toString, "http method")
    resp.request.headers.toSeq.sortBy(_._1).foreach(
      entry => entries = entries :+ RecordingEntry(RequestRecordingEntryTypes.STRING, s"${entry._1}=${entry._2}" ,"request header")
    )
    resp.responseHeaders.toSeq.sortBy(_._1).foreach(
      entry => entries = entries :+ RecordingEntry(RequestRecordingEntryTypes.STRING, s"${entry._1}=${entry._2}" ,"response header")
    )
    (body.isDefined) match {
      case true => entries = entries :+ RecordingEntry(RequestRecordingEntryTypes.JSON, mapper.writeValueAsString(body.get), "response body")
      case _ =>
    }
    entries
  }
}
