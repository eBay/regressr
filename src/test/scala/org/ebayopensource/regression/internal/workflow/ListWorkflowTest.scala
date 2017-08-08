package org.ebayopensource.regression.internal.workflow

import org.ebayopensource.regression.UnitSpec
import org.ebayopensource.regression.internal.datastore.TestDataStore

/**
  * Created by asfernando on 6/5/17.
  */
class ListWorkflowTest extends UnitSpec {

  "A list workflow " should "list all the recordings present" in {
    val recordingList = Seq("recording1", "recording2", "recording3")
    val testDataStore = new TestDataStore( Some( () => recordingList ), None)
    assert(new ListWorkflow(testDataStore).listWorkflows() == recordingList)
  }

  "A list workflow " should "return no recordings found message" in {
    val recordingList = Seq()
    val testDataStore = new TestDataStore( Some( () => recordingList ), None)
    assert(new ListWorkflow(testDataStore).listWorkflows() == Seq("No recordings were found"))
  }
}
