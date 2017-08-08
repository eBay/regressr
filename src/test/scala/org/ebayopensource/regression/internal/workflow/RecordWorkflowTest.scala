package org.ebayopensource.regression.internal.workflow

import java.util

import org.ebayopensource.regression.UnitSpec
import org.ebayopensource.regression.internal.datastore.TestDataStore
import org.ebayopensource.regression.internal.http.{SimpleHttpClient, SimpleHttpClientTest}
import org.ebayopensource.regression.internal.reader._

/**
  * Created by asfernando on 8/5/17.
  */
class RecordWorkflowTest extends UnitSpec {

  val returnCode = 200

  "A Record Workflow" should "complete successfully with a simple GET request" in {
    val dataStore = new TestDataStore(None, None)
    val mockClient = new SimpleHttpClient(new SimpleHttpClientTest().getClient(returnCode, "Test Body", Map()))
    val testIdentifier = "simpleget"
    val requestEntry = new RequestEntry(
      "req1",
      "/",
      "GET",
      new util.HashMap[String, String](),
      null,
      "org.ebayopensource.regression.internal.components.recorder.SimpleHTTPJSONRecorder",
      null,
      "org.ebayopensource.regression.internal.components.comparator.SimpleHTTPJsonComparator",
      new util.HashMap[String, String](),
      null
    )
    val requestEntryList = new util.ArrayList[RequestEntry]()
    requestEntryList.add(requestEntry)
    val strategy = new TestStrategy(new Service("http://wwww.someservice.com/"),
      new java.util.HashMap(), requestEntryList)

    new RecordWorkflow(dataStore, mockClient).recordState(testIdentifier, strategy)
    assert(true)
  }
}
