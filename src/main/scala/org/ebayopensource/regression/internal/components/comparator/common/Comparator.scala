package org.ebayopensource.regression.internal.components.comparator.common

import org.ebayopensource.regression.internal.components.recorder.common.RequestRecordingEntry

import scala.util.Try

/**
  * An implementation of the Comparator compares two recorded entries per request. The recorded and the replayed.
  * Write your own comparator to compare two requests at your own level of meaningful abstraction.
  *
  * Hint: You might not want to compare fields that change for every request such as an header that sends the current
  * time for example.
  */
abstract class Comparator {

  def compare(recorded: RequestRecordingEntry, replayed: RequestRecordingEntry): Try[Seq[CompareMessage]]

}
