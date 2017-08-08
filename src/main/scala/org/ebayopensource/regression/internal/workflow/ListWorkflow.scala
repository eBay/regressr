package org.ebayopensource.regression.internal.workflow

import org.ebayopensource.regression.internal.datastore.BaseDataStore


/**
  * Created by asfernando on 4/23/17.
  */
class ListWorkflow(dataStore: BaseDataStore) {

  def listWorkflows(): Seq[String] = {
    val strategies = dataStore.listStrategies()
    if (strategies.size==0) Seq("No recordings were found") else strategies
  }
}
