package org.ebayopensource.regression.internal.components.requestBuilder

import scala.util.Try

/**
  * Use an implementation of the request payload builder when you want to send a payload to the service
  * as in the cases of a HTTP POST or PUT request.
  *
  * The payload must be converted to String before returning it back.
  */
abstract class RequestPayloadBuilder {

  def buildRequest(dataInput: Map[String, String]): Try[String]

}
