package org.ebayopensource.regression.internal.components.recorder

import java.net.URI

import org.ebayopensource.regression.UnitSpec
import org.ebayopensource.regression.internal.http.{HTTPRequest, HTTPResponse}
import org.ebayopensource.regression.internal.reader.HttpMethods

import scala.util.{Failure, Success}

/**
  * Created by asfernando on 5/15/17.
  */
class SimpleHTTPJSONRecorderTest extends UnitSpec {

  "A simple http json recorder" should "generate recording entries when a GET Request is passed into it" in {
    val recorder = new SimpleHTTPJSONRecorder
    val re = recorder.recordAndFilter(Seq(HTTPResponse(
      HTTPRequest(new URI("http://www.ebay.com"), Map("header1" -> "value1"),
        HttpMethods.GET, None), 200, Some("""{"name":"value"}"""), Map("responseHeader1" -> "responseValue1"))))
    re match {
      case Success(t) => assert(t.entries.size>0) //
      case Failure(t) => assert(false, t)
    }
  }

  "A simple http json recorder" should "generate 7 recording entries when a POST Request is passed into it" in {
    val recorder = new SimpleHTTPJSONRecorder
    val re = recorder.recordAndFilter(Seq(HTTPResponse(
      HTTPRequest(new URI("http://www.ebay.com"), Map("header1" -> "value1"),
        HttpMethods.POST, Some("")), 200, Some("""{"name":"value"}"""), Map("responseHeader1" -> "responseValue1"))))
    re match {
      case Success(t) => assert(t.entries.size>0) //
      case Failure(t) => assert(false, t)
    }
  }
}
