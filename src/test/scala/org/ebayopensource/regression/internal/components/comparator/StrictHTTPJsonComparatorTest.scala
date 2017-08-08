package org.ebayopensource.regression.internal.components.comparator

import org.ebayopensource.regression.UnitSpec

/**
  * Created by asfernando on 7/17/17.
  */
class StrictHTTPJsonComparatorTest extends UnitSpec {

  "When two identical JSONs are passed, the comparator" should "should not throw no error messages" in {
    val json1 = "{'name1':'value1','name2':'value2'}"
    val json2 = "{'name1':'value1','name2':'value2'}"
    assert(new StrictHTTPJsonComparator().getJSONComparisonErrors(json1, json2).isEmpty)
  }

  "When two almost identical JSONs are passed with changes in values, the comparator" should "should throw error messages" in {
    val json1 = "\"{'name1':'value1','name2':'value1'}\""
    val json2 = "\"{'name1':'value1','name2':'value2'}\""
    val messages = new StrictHTTPJsonComparator().getJSONComparisonErrors(json1, json2)
    assert(messages.isDefined)
    assert(messages.get.contains("Expected: value1"))
    assert(messages.get.contains("got: value2"))
  }
}
