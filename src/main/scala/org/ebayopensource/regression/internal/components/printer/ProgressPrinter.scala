package org.ebayopensource.regression.internal.components.printer

import org.ebayopensource.regression.internal.http.HTTPResponse

/**
  * Created by asfernando on 4/30/17.
  */
trait ProgressPrinter {

  def printProgress(response: HTTPResponse) : Unit

}
