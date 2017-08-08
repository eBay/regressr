package org.ebayopensource.regression.example

import java.net.URI

import org.ebayopensource.regression.internal.components.continuation.Continuation
import org.ebayopensource.regression.internal.http.{HTTPRequest, HTTPResponse}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper

import scala.util.Try

/**
  * Created by asfernando on 6/8/17.
  */
class HATEOASContinuation extends Continuation {

  override def getContinuations(resp: HTTPResponse): Try[Seq[HTTPRequest]] = Try {
    val request = resp.request

    if (request.url.toString.endsWith("shopping/search/shoes")) {
      val map = getMap(resp.body.get)
      val items = map.get("items").get.asInstanceOf[List[String]]
      items.map(url => HTTPRequest(new URI(url), request.headers, request.method, None))
    }
    else if (request.url.toString.matches("(.)*/endpoint/shopping/items/item[0-9]")) {
      val map = getMap(resp.body.get)
      Seq(HTTPRequest(new URI(map.get("seller").get.asInstanceOf[String]), request.headers, request.method, None))
    }
    else {
      Seq()
    }
  }

  def getMap(json: String) : Map[String, Object] = {
    val mapper = new ObjectMapper() with ScalaObjectMapper
    mapper.registerModule(DefaultScalaModule)
    mapper.readValue[Map[String, Object]](json)
  }
}

