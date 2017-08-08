package org.ebayopensource.regression.internal.components.comparator

import org.ebayopensource.regression.internal.common.Util
import org.ebayopensource.regression.internal.components.comparator.common.{Comparator, CompareMessage}
import org.ebayopensource.regression.internal.components.recorder.common.{RecordingEntry, RequestRecordingEntry, RequestRecordingEntryTypes}
import org.ebayopensource.regression.json.CustomJsonAssert

import scala.collection.mutable.ListBuffer
import scala.util.Try


/**
  * Created by asfernando on 4/18/17.
  */
class SimpleHTTPJsonComparator extends Comparator {

  override def compare(recorded: RequestRecordingEntry, replayed: RequestRecordingEntry): Try[Seq[CompareMessage]] = Try {
    if (recorded.entries.size != replayed.entries.size) {
      Seq(CompareMessage(s"Recorded entries and replayed entries were not equal ${recorded.entries.size} vs ${replayed.entries.size}.",
        recorded.entries.size.toString, replayed.entries.size.toString))
    }
    else {
      val entries: Seq[(RecordingEntry, RecordingEntry)] = {
        for ((recordedEntry, replayedEntry) <- recorded.entries zip replayed.entries) yield (recordedEntry, replayedEntry)
      }

      val compareDataSeq = ListBuffer[CompareMessage]()
      entries.foreach {
        case (recorded, replayed) => {
          if (recorded.entryType != replayed.entryType) {
            compareDataSeq += CompareMessage(s"Recorded entry type and replayed entry type do not match ${recorded.entryType} vs ${replayed.entryType}",
              recorded.entryType.toString, replayed.entryType.toString)
          }
          else {
            recorded.entryType match {
              case RequestRecordingEntryTypes.JSON => {
                getJSONComparisonErrors(recorded.data, replayed.data).foreach {
                  entry => {
                    entry.split(" ; ").foreach {
                      token => compareDataSeq += CompareMessage(token, recorded.data.replace("\\", ""), replayed.data.replace("\\", ""))
                    }
                  }
                }
              }
              case RequestRecordingEntryTypes.STRING => {
                if (!recorded.data.equals(replayed.data)) {
                  compareDataSeq += CompareMessage(s"The ${recorded.description} did not match between recorded and replayed. " +
                    s"${recorded.data} vs ${replayed.data}", recorded.data, replayed.data)
                }
              }
            }
          }
        }
      }

      compareDataSeq
    }
  }

  val mapper = Util.getMapper()

  def getJSONComparisonErrors(prev: String, newOne: String): Option[String] = {
    var cleansedOne = prev.replaceAll("\\\\","")
    var cleansedTwo = newOne.replaceAll("\\\\","")
    cleansedOne = if (cleansedOne.startsWith("\"")) cleansedOne.substring(1, cleansedOne.length-1) else cleansedOne
    cleansedTwo = if (cleansedTwo.startsWith("\"")) cleansedTwo.substring(1, cleansedTwo.length-1) else cleansedTwo
    val tryingComparison = Try(CustomJsonAssert.assertEquals(cleansedOne, cleansedTwo, false))
    if (tryingComparison.isFailure) Some(tryingComparison.failed.get.getMessage) else None
  }
}
