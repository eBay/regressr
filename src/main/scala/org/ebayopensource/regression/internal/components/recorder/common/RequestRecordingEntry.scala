package org.ebayopensource.regression.internal.components.recorder.common

/**
  * Created by asfernando on 4/18/17.
  */
case class RequestRecordingEntry(entries: Seq[RecordingEntry])

case class RecordingEntry(entryType: RequestRecordingEntryTypes.Value, data: String, description: String)

object RequestRecordingEntryTypes extends Enumeration {
  val JSON, STRING = Value
}


