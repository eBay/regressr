package org.ebayopensource.regression.internal.http

/**
  * Created by asfernando on 4/17/17.
  */
case class HTTPResponse(request: HTTPRequest, status: Int, body: Option[String], responseHeaders: Map[String, String])
