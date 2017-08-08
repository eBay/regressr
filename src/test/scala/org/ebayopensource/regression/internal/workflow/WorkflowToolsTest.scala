package org.ebayopensource.regression.internal.workflow

import java.net.URI

import org.ebayopensource.regression.UnitSpec
import org.ebayopensource.regression.internal.http.{BaseHttpClient, HTTPRequest, HTTPResponse}
import org.ebayopensource.regression.internal.reader.{HttpMethods, YAMLTestStrategyReader}
import org.mockito.Matchers._

import scala.io.Source
import scala.util.{Failure, Success, Try}

/**
  * Created by asfernando on 7/16/17.
  */
class WorkflowToolsTest extends UnitSpec {

  val SUCCESS_CODE = 200

  "When passed in a request and a client, performRequest" should "return a valid http response" in {
    val client = mock[BaseHttpClient]
    (client.execute _).expects(*).returns(Try(HTTPResponse(any[HTTPRequest], SUCCESS_CODE, any[Option[String]], any[Map[String, String]])))

    val strategyContent = Source.fromInputStream(getClass.getResourceAsStream("/yaml/valid_resource_perform_request.yaml")).mkString.replace("\t","")

    YAMLTestStrategyReader.read(strategyContent) match {
      case Success(t) => assert(WorkflowTools.performRequest("test1", t, t.requests.head, client).isSuccess)
      case Failure(t) => assert(false)
    }
  }

  "When a request entry, http client and a continuation is passed, performContinuations" should "return zero or more requests" in {

    val firstResponse = Source.fromInputStream(getClass.getResourceAsStream("/httpResponse/continuationResponse.txt")).mkString.replace("\t", "")

    val request = HTTPRequest(new URI("/urlthatdoesnotexist"), Map[String, String](), HttpMethods.POST, None)

    val client = mock[BaseHttpClient]
    (client.execute _).expects(*).returns(Try(HTTPResponse(request, SUCCESS_CODE, Some(firstResponse), any[Map[String, String]])))

    val strategyContent = Source.fromInputStream(getClass.getResourceAsStream("/yaml/valid_one_simple_continuation.yaml")).mkString.replace("\t", "")

    YAMLTestStrategyReader.read(strategyContent) match {
      case Success(t) => {
        val res = WorkflowTools.performContinuations("test1", t, t.requests.head, client)
        assert(res.isSuccess)
        assert(res.get.size==1)
      }
      case Failure(t) => assert(false)
    }
  }
}
