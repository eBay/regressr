package org.ebayopensource.regression.internal.components.recorder.common

import org.ebayopensource.regression.internal.http.HTTPResponse

import scala.util.{Failure, Success, Try}

/**
  * The Recorder is used to specify what needs to be recorded given a sequence of HTTPResponses. Most of the time
  * there is only one HTTPResponse supplied to the Recorder i.e. the current request's.
  *
  * However in the case of continuations, there might be many responses that come back depending on the
  * continuation's logic. In this case the recorder is expected to handle the case appropriately.
  */
abstract class Recorder {

  def recordAndFilter(responses: Seq[HTTPResponse]) : Try[RequestRecordingEntry] = Try {
    RequestRecordingEntry(record(responses) match {
      case Success(rre) => {
        rre.entries.map {
          recordingEntry => {
            if (Option(recordingEntry.data).isDefined) {
              recordingEntry
            }
            else {
              RecordingEntry(recordingEntry.entryType, "<value-was-not-resolved-please-check-recorder>", recordingEntry.description)
            }
          }
        }
      }
      case Failure(t) => throw t
    })
  }

  protected def record(responses: Seq[HTTPResponse]) : Try[RequestRecordingEntry]

}
