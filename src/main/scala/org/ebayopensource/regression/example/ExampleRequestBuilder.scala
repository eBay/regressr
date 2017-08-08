package org.ebayopensource.regression.example

import org.ebayopensource.regression.internal.components.requestBuilder.RequestPayloadBuilder

import scala.util.Try

/**
  * Created by asfernando on 5/25/17.
  */
class ExampleRequestBuilder extends RequestPayloadBuilder {

  override def buildRequest(dataInput: Map[String, String]): Try[String] = Try {
    s"""{"mission": "${dataInput.get("conversationId").get}",
       |"keyword" : "${dataInput.get("keyword").get}", "mission_start": "${dataInput.get("mission_start").get}" } """.stripMargin
  }
}
