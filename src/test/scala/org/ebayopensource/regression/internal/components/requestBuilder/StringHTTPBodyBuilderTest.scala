package org.ebayopensource.regression.internal.components.requestBuilder

import org.ebayopensource.regression.UnitSpec

/**
  * Created by asfernando on 5/31/17.
  */
class StringHTTPBodyBuilderTest extends UnitSpec {

  "A String HTTP Body builder " should "be able to return a string when present as a 'payload' input" in {
    val payload = """{"name":"eBay","years":20}"""
    val returnTry = new StringHTTPBuilder().buildRequest(Map("payload" -> payload))
    assert(returnTry.isSuccess)
    assert(returnTry.get == payload)
  }

  "A String HTTP Body builder " should "throw an exception when 'payload' is not present" in {
    val payload = """{"name":"eBay","years":20}"""
    val returnTry = new StringHTTPBuilder().buildRequest(Map("payloads" -> payload))
    assert(returnTry.isFailure)
    assert(returnTry.failed.get.getMessage.contains("payload"))
  }

}
