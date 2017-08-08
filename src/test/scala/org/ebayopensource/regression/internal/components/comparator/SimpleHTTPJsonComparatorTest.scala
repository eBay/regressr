package org.ebayopensource.regression.internal.components.comparator

import org.ebayopensource.regression.UnitSpec
import org.ebayopensource.regression.internal.components.recorder.common.{RecordingEntry, RequestRecordingEntry}

import scala.util.{Failure, Success}
import org.ebayopensource.regression.internal.components.recorder.common.RequestRecordingEntryTypes._

/**
  * Created by asfernando on 5/15/17.
  */
class SimpleHTTPJsonComparatorTest extends UnitSpec {

  "A Simple HTTP and JSON Comparator" should "be able to compare two empty entries and not report any errors" in {
    val comparator = new SimpleHTTPJsonComparator
    comparator.compare(RequestRecordingEntry(Seq()), RequestRecordingEntry(Seq())) match {
      case Success(seq) => assert(seq.size==0)
      case Failure(t) => assert(false, "Unexpected exception " + t)
    }
  }

  "A Simple HTTP and JSON Comparator" should "complain when two types are not the same" in {
    val comparator = new SimpleHTTPJsonComparator
    val re = RecordingEntry(STRING, "simpleString", "A simple string")
    val re2 = RecordingEntry(JSON, "simpleString", "A simple string")
    comparator.compare(RequestRecordingEntry(Seq(re)), RequestRecordingEntry(Seq(re2))) match {
      case Success(seq) => {
        assert(seq.size == 1)
        assert(seq(0).message == "Recorded entry type and replayed entry type do not match STRING vs JSON")
      }
      case Failure(t) => assert(false, "Unexpected exception " + t)
    }
  }

  "A Simple HTTP and JSON Comparator" should "complain when the sizes of both lists are not the same" in {
    val comparator = new SimpleHTTPJsonComparator
    val re = RecordingEntry(STRING, "simpleString", "A simple string")
    val re21 = RecordingEntry(JSON, "simpleString", "A simple string")
    val re22 = RecordingEntry(JSON, "simpleString2", "A simple JSON")
    comparator.compare(RequestRecordingEntry(Seq(re)), RequestRecordingEntry(Seq(re21, re22))) match {
      case Success(seq) => {
        assert(seq.size == 1)
        assert(seq(0).message == "Recorded entries and replayed entries were not equal 1 vs 2.")
      }
      case Failure(t) => assert(false, "Unexpected exception " + t)
    }
  }

  "A Simple HTTP and JSON Comparator" should "be able to compare two equal string entries and not report errors" in {
    val comparator = new SimpleHTTPJsonComparator
    val re = RecordingEntry(STRING, "simpleString", "A simple string")
    val re2 = RecordingEntry(STRING, "simpleString", "A simple string")
    comparator.compare(RequestRecordingEntry(Seq(re)), RequestRecordingEntry(Seq(re2))) match {
      case Success(seq) => assert(seq.size==0)
      case Failure(t) => assert(false, "Unexpected exception " + t)
    }
  }

  "A Simple HTTP and JSON Comparator" should "be able to compare two unequal string entries and report a single error." in {
    val comparator = new SimpleHTTPJsonComparator
    val re = RecordingEntry(STRING, "unequalSimpleString", "An unequal simple string")
    val re2 = RecordingEntry(STRING, "simpleString", "A simple string")
    comparator.compare(RequestRecordingEntry(Seq(re)), RequestRecordingEntry(Seq(re2))) match {
      case Success(seq) => assert(seq.size==1)
      case Failure(t) => assert(false, "Unexpected exception " + t)
    }
  }

  "A Simple HTTP and JSON Comparator" should "be able to compare two equal JSON Entries and not report errors" in {
    val comparator = new SimpleHTTPJsonComparator
    val re = RecordingEntry(JSON, """{"name":"json"}""", "A simple JSON")
    val re2 = RecordingEntry(JSON, """{"name":"json"}""", "A simple JSON")
    comparator.compare(RequestRecordingEntry(Seq(re)), RequestRecordingEntry(Seq(re2))) match {
      case Success(seq) => assert(seq.size==0)
      case Failure(t) => assert(false, "Unexpected exception " + t)
    }
  }

  "A Simple HTTP and JSON Comparator" should "be able to compare two structurally equal JSON Entries and not report errors" in {
    val comparator = new SimpleHTTPJsonComparator
    val re = RecordingEntry(JSON, """{"name":"json1"}""", "A simple JSON")
    val re2 = RecordingEntry(JSON, """{"name":"json"}""", "A simple JSON")
    comparator.compare(RequestRecordingEntry(Seq(re)), RequestRecordingEntry(Seq(re2))) match {
      case Success(seq) => assert(seq.size==0)
      case Failure(t) => assert(false, "Unexpected exception " + t)
    }
  }

  "A Simple HTTP and JSON Comparator" should "be able to compare two structurally different JSON Entries and report errors" in {
    val comparator = new SimpleHTTPJsonComparator
    val re = RecordingEntry(JSON, """{"name":"json1"}""", "A simple JSON")
    val re2 = RecordingEntry(JSON, """{"names":"json"}""", "A structurally different JSON")
    comparator.compare(RequestRecordingEntry(Seq(re)), RequestRecordingEntry(Seq(re2))) match {
      case Success(seq) => assert(seq.size==2 /*Remember that key renames will result in 2 comparision messages*/ )
      case Failure(t) => assert(false, "Unexpected exception " + t)
    }
  }
}
