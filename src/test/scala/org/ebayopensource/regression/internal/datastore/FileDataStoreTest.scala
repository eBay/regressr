package org.ebayopensource.regression.internal.datastore

import org.ebayopensource.regression.UnitSpec
import org.ebayopensource.regression.internal.components.recorder.common.{RecordingEntry, RequestRecordingEntry, RequestRecordingEntryTypes}

/**
  * Created by asfernando on 7/17/17.
  */
class FileDataStoreTest extends UnitSpec {

  "A file datastore" should "be able to store strategies" in {
    val dataStore = new FileDataStore("/tmp/")
    dataStore.storeStrategy("test1", "content1")
    val strategy = dataStore.getStrategy("test1")
    assert(strategy.isDefined)
    assert(strategy === Some("content1"))
    dataStore.deleteStrategy("test1")
    assert(dataStore.getStrategy("test1").isEmpty)
  }

  "A file datastore" should "be able to store and retrieve recordings" in {
    val dataStore = new FileDataStore("/tmp/")
    dataStore.storeRequestRecording("test1", "request1",
      RequestRecordingEntry(Seq(RecordingEntry(RequestRecordingEntryTypes.STRING, "a simple string", "a simple string"))))
    assert(dataStore.getRequestRecording("test1", "request1").isSuccess)
    dataStore.deleteRecording("test1")
    assert(dataStore.getRequestRecording("test1", "request1").isFailure)
  }
}
