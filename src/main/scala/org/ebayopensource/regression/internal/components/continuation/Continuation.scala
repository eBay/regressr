package org.ebayopensource.regression.internal.components.continuation

import org.ebayopensource.regression.internal.http.{HTTPRequest, HTTPResponse}

import scala.util.Try

/**
  * A continuation is used when you want to implement a series of requests that needs to be performed programmatically
  * rather than hardcoding requests in the configuration.
  *
  * The continuation accepts a response and returns a sequence of map entries (dataInput) that are used to make
  * subsequent requests with the same builder that was used in the original request.
  *
  * An example usage of a continuation would be to traverse RESTFul resources that have an HATEOAS traversal mechanism.
  */
abstract class Continuation {

  def getContinuations(resp: HTTPResponse) : Try[Seq[HTTPRequest]]

}
