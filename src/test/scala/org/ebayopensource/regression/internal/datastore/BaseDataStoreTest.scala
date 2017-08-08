package org.ebayopensource.regression.internal.datastore

import org.ebayopensource.regression.UnitSpec
import org.ebayopensource.regression.internal.components.recorder.common.{RecordingEntry, RequestRecordingEntry}
import org.ebayopensource.regression.internal.components.recorder.common.RequestRecordingEntryTypes._

/**
  * Created by asfernando on 5/16/17.
  */
class BaseDataStoreTest extends UnitSpec {

  "A base data store" should "be able to store and retrieve the content of a strategy file" in {
    val store = new TestDataStore(None, None)
    store.storeStrategy("Y15W17","content")
    assert(store.getStrategy("Y15W17").get == "content")
  }

  "A base data store" should "be able to store and delete the content of a strategy file" in {
    val store = new TestDataStore(None, None)
    store.storeStrategy("Y15W17","content")
    assert(store.getStrategy("Y15W17").get == "content")
    store.deleteStrategy("Y15W17")
    assert(store.getStrategy("Y15W17").isEmpty)
  }

  "A base data store" should "be able to store and retrieve a request recording entry" in {
    val store = new TestDataStore(None, None)
    val rre = RequestRecordingEntry(Seq(RecordingEntry(STRING, "The brown fox jumps over the lazy dog", "string description")))
    store.storeRequestRecording("Y15W17", "req1", rre)
    val rre2 = store.getRequestRecording("Y15W17", "req1")
    assert(rre == rre2.get)
  }
}
