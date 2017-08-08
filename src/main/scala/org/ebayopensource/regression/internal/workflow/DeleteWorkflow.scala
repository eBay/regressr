package org.ebayopensource.regression.internal.workflow

import org.ebayopensource.regression.internal.datastore.BaseDataStore

/**
  * Created by asfernando on 6/2/17.
  */
class DeleteWorkflow(dataStore: BaseDataStore) {

  def delete(args: Array[String]) : Seq[String] = {
    args.filter(dataStore.getStrategy(_).isDefined).map {
      arg => {
          dataStore.deleteRecording(arg)
          arg
      }
    }
  }
}
