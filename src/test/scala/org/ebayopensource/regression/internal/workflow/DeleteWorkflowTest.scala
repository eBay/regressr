package org.ebayopensource.regression.internal.workflow

import org.ebayopensource.regression.UnitSpec
import org.ebayopensource.regression.internal.datastore.{BaseDataStore, TestDataStore}

import scala.util.Try

/**
  * Created by asfernando on 6/5/17.
  */
class DeleteWorkflowTest extends UnitSpec {

  "A delete workflow" should "be able to delete recordings" in {
    val validRecordings = Seq("record1", "record2", "record3")
    val invalidRecordings = Seq("record4","record5")
    val dataStore = new TestDataStore(None, Some( (e: String) => Try { s"${e}" } ))
    validRecordings.foreach( recording => dataStore.put(BaseDataStore.getStrategyContentKey(recording), ""))

    val deletedRecordings = new DeleteWorkflow(dataStore).delete( (validRecordings ++ invalidRecordings).toArray )

    assert(deletedRecordings == validRecordings)
  }
}
