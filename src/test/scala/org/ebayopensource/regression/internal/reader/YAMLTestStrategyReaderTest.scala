package org.ebayopensource.regression.internal.reader

import org.ebayopensource.regression.UnitSpec

import scala.io.Source
import scala.util.{Failure, Success}

/**
  * Created by asfernando on 5/15/17.
  */
class YAMLTestStrategyReaderTest extends UnitSpec {

  "A reader" should "be able to read a valid strategy file with 1 request" in {
    val strategyContent = Source.fromInputStream(getClass.getResourceAsStream("/yaml/valid_strategy_simple_one_request.yaml")).mkString.replace("\t","")
    YAMLTestStrategyReader.read(strategyContent) match {
      case Success(t) => {
        assert(t.requests.size == 1)
        assert(t.headers.size>0)
      }
      case Failure(t) => assert(false, s"Strategy file was valid. Should not throw exception ${t.getMessage}")
    }
  }

}
