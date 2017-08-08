package org.ebayopensource.regression.json

import org.ebayopensource.regression.UnitSpec

/**
  * Created by asfernando on 5/7/17.
  */
class JSONCleanserTest extends UnitSpec {

  "A Json cleanser" should "cleanse double quotes in text JSON" in {
    val json = """{"name":"Ashwa\"nth"}"""
    val cleansed = JSONCleanser.getCleansedJSON(json)
    assert(cleansed == """{"name":"Ashwa'nth"}""")
  }

  "A Json cleanser" should "clean double quotes in array JSON" in {
    val json = """{"name":["Ashwa\"nth"]}"""
    val cleansed = JSONCleanser.getCleansedJSON(json)
    assert(cleansed == """{"name":["Ashwa'nth"]}""")
  }

  "A Json cleanser" should "not do anything when the input JSON is already clean" in {
    val json = """{"name":["Ashwanth"]}"""
    val cleansed = JSONCleanser.getCleansedJSON(json)
    assert(cleansed == """{"name":["Ashwanth"]}""")
  }

}
