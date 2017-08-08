package org.ebayopensource.regression.internal.http

import scala.util.Try

/**
  * Created by asfernando on 6/5/17.
  */
abstract class BaseHttpClient() {

  def execute(testRequest: HTTPRequest): Try[HTTPResponse]

}
