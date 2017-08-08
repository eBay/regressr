package org.ebayopensource.regression.internal.components.comparator

import org.ebayopensource.regression.json.CustomJsonAssert

import scala.util.Try

/**
  * Created by asfernando on 6/8/17.
  */
class StrictHTTPJsonComparator extends SimpleHTTPJsonComparator {

  override def getJSONComparisonErrors(prev: String, newOne: String): Option[String] = {
    var cleansedOne = prev.replaceAll("\\\\","")
    var cleansedTwo = newOne.replaceAll("\\\\","")
    cleansedOne = if (cleansedOne.startsWith("\"")) cleansedOne.substring(1, cleansedOne.length-1) else cleansedOne
    cleansedTwo = if (cleansedTwo.startsWith("\"")) cleansedTwo.substring(1, cleansedTwo.length-1) else cleansedTwo
    val tryingComparison = Try(CustomJsonAssert.assertEquals(cleansedOne, cleansedTwo, true))
    if (tryingComparison.isFailure) Some(tryingComparison.failed.get.getMessage) else None
  }
}
