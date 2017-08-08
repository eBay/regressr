package org.ebayopensource.regression.internal.http

import java.net.URI

import org.ebayopensource.regression.UnitSpec
import org.ebayopensource.regression.internal.reader.HttpMethods

import scalaj.http.{BaseHttp, HttpOptions, HttpRequest, HttpResponse}

/**
  * Created by asfernando on 5/1/17.
  */
// scalastyle:off
class SimpleHttpClientTest extends UnitSpec {

  def getClient(code: Int, body: String, headers: Map[String, IndexedSeq[String]]) : BaseHttp = {
    val client = mock[BaseHttp]

    val httpRequest = mock[HttpRequest]
    (httpRequest.headers(_:Map[String, String])).expects(*).returns(httpRequest)
    (httpRequest.method(_:String)).expects(*).returns(httpRequest)
    (httpRequest.option(_:HttpOptions.HttpOption)).expects(*).returns(httpRequest)
    (httpRequest.postData(_:String)).expects(*).returns(httpRequest)
    (httpRequest.timeout(_:Int, _:Int)).expects(*,*).returns(httpRequest)
    (httpRequest.asString _).expects().returns(HttpResponse[String](body, code, headers))

    (client.apply _).expects(*).returns(httpRequest)
    client
  }

  "A simple HTTP client" should "return the HTTP response when successful" in {
    val responseBody = """{"name":"regressr"}"""
    val client = new SimpleHttpClient(getClient(200, responseBody, Map()))
    val response = client.execute(HTTPRequest(new URI(""), Map(), HttpMethods.GET, Some("")))
    assert(response.isSuccess)
    assert(response.get.status == 200)
    assert(response.get.body.isDefined)
    assert(response.get.body.get == responseBody)
  }
}
// scalastyle:on
