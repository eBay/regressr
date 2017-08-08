package org.ebayopensource.regression.internal.reader

import scala.util.Try

/**
  * Created by asfernando on 4/17/17.
  */
trait TestStrategyReader {

  def read(content: String): Try[TestStrategy]

}
