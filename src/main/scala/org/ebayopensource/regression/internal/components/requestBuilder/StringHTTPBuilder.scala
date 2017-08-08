package org.ebayopensource.regression.internal.components.requestBuilder
import scala.util.Try

/**
  * Created by asfernando on 5/31/17.
  */
class StringHTTPBuilder extends RequestPayloadBuilder {

  override def buildRequest(dataInput: Map[String, String]): Try[String] = Try {
    val contentOption = dataInput.get("payload")

    if (contentOption.isEmpty) throw new IllegalArgumentException("'payload' must be present while using StringHTTPBodyBuilder")

    contentOption.get
  }
}
