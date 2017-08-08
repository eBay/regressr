package org.ebayopensource.regression.internal.http

import java.net.URI
import org.ebayopensource.regression.internal.reader.HttpMethods

/**
  * Created by asfernando on 4/17/17.
  */
case class HTTPRequest(url: URI, headers: Map[String, String], method: HttpMethods.Value, body: Option[String])
