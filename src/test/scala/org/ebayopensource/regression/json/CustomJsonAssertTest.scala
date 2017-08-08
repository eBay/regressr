package org.ebayopensource.regression.json

import org.ebayopensource.regression.UnitSpec

import scala.util.{Failure, Success, Try}

/**
  * Created by asfernando on 5/15/17.
  */
class CustomJsonAssertTest extends UnitSpec {

  "A custom JSON Asserter" should "be able to ignore regressions in values when compareValues is turned off" in {
    val json1 = """{"name":"eBay", "yearsInBusiness":"20"}"""
    val json2 = """{"name":"eBay", "yearsInBusiness":"30"}"""

    Try[Unit] (CustomJsonAssert.assertEquals(json1, json2, false /*Do not compare values*/)) match {
      case Success(t) => assert(true)
      case Failure(t) => assert(false, "Assert threw an exception which was not expected:= " + t.getMessage)
    }
  }

  "A custom JSON Asserter" should "be able to detect regressions in values when compareValues is turned on" in {
    val json1 = """{"name":"eBay", "yearsInBusiness":"20"}"""
    val json2 = """{"name":"eBay", "yearsInBusiness":"30"}"""

    Try[Unit] (CustomJsonAssert.assertEquals(json1, json2, true /*Do not compare values*/)) match {
      case Success(t) => assert(false, "Expected JSON Excepion due to regression in value.")
      case Failure(t) => assert(t.isInstanceOf[AssertionError])
    }
  }

  "A custom JSON Asserter" should "be able to ignore regressions in JSON Arrays that have ONLY simple values" in {
    val json1 = """{"name":"eBay", "languages":["java","scala","python"]}"""
    val json2 = """{"name":"eBay", "languages":["java","scala","pythons","haskell"]}"""

    Try[Unit] (CustomJsonAssert.assertEquals(json1, json2, false, false)) match {
      case Success(t) => assert(true)
      case Failure(t) => assert(false, "Assert threw an exception which was not expected:= " + t.getMessage)
    }
  }

  "A custom JSON Asserter" should "be able to detect regressions in JSON Arrays that have ONLY simple values when compareJSONArrays is turned on" in {
    val json1 = """{"name":"eBay", "languages":["java","scala","python"]}"""
    val json2 = """{"name":"eBay", "languages":["java","scala","pythons","haskell"]}"""

    Try[Unit] (CustomJsonAssert.assertEquals(json1, json2, false, true)) match {
      case Success(t) => assert(false, "Expected an exception in array values comparision.")
      case Failure(t) => assert(t.isInstanceOf[AssertionError])
    }
  }

  "A custom JSON Asserter" should "default behavior should be to compare JSON Arrays" in {
    val json1 = """{"name":"eBay", "languages":["java","scala","python"]}"""
    val json2 = """{"name":"eBay", "languages":["java","scala","pythons","haskell"]}"""

    Try[Unit] (CustomJsonAssert.assertEquals(json1, json2, false)) match {
      case Success(t) => assert(false, "Expected an exception in array values comparision.")
      case Failure(t) => assert(t.isInstanceOf[AssertionError])
    }
  }

  "A custom JSON Asserter" should "be able to ignore ANY regressions in JSONArrays when compareJSONArrays is turned off " in {
    val json1 = """{"name":"eBay", "departments":[{"name":"core"},{"name":"n"}]}"""
    val json2 = """{"name":"eBay", "departments":[{"name":"core"},{"name":"npd"}]}"""

    Try[Unit] (CustomJsonAssert.assertEquals(json1, json2, true, false)) match {
      case Success(t) => assert(true)
      case Failure(t) => {
        assert(false, s"Should not have thrown exception for value regression. ${t}")
      }
    }
  }

  "A custom JSON Asserter" should "be able to detect deep level regressions in values" in {
    val json1 = """{"name":"eBay", "department":{"name":"npd","size":"50"}}"""
    val json2 = """{"name":"eBay", "department":{"name":"npd","size":"60"}}"""

    Try[Unit] (CustomJsonAssert.assertEquals(json1, json2, true)) match {
      case Success(t) => assert(false, "Expected an exception in values comparision.")
      case Failure(t) => assert(t.isInstanceOf[AssertionError])
    }
  }

  "A custom JSON Asserter" should "be able to detect deep level regressions in element names" in {
    val json1 = """{"name":"eBay", "department":{"name":"npd","size":"50"}}"""
    val json2 = """{"name":"eBay", "department":{"name":"npd","sizes":"60"}}"""

    Try[Unit] (CustomJsonAssert.assertEquals(json1, json2, false)) match {
      case Success(t) => assert(false, "Expected an exception in element name comparision.")
      case Failure(t) => assert(t.isInstanceOf[AssertionError])
    }
  }

  "A custom JSON Asserter" should "be able to detect extensions in JSON" in {
    val json1 = """{"name":"eBay", "department":{"name":"npd","size":"50"}}"""
    val json2 = """{"name":"eBay", "department":{"name":"npd","size":"50"}, "age":"20"}"""

    Try[Unit] (CustomJsonAssert.assertEquals(json1, json2, false)) match {
      case Success(t) => assert(false, "Expected an exception in element name comparision.")
      case Failure(t) => assert(t.isInstanceOf[AssertionError])
    }
  }
}
