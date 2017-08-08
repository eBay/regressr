package org.ebayopensource.regression.internal.reader

import java.io.StringReader

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper

import scala.util.Try

/**
  * Created by asfernando on 4/17/17.
  * http://stackoverflow.com/questions/19441400/working-with-yaml-for-scala
  */
object YAMLTestStrategyReader extends TestStrategyReader with App {

  override def read(content: String): Try[TestStrategy] = Try {
    val reader = new StringReader(content)
    val mapper = new ObjectMapper(new YAMLFactory()) with ScalaObjectMapper
    mapper.registerModule(DefaultScalaModule)
    mapper.readValue[TestStrategy](reader)
  }
}

