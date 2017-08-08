package org.ebayopensource.regression.internal.components.requestBuilder

import java.io.File

import scala.io.Source
import scala.util.Try

/**
  * Created by asfernando on 5/16/17.
  */
class FileHTTPBodyBuilder extends RequestPayloadBuilder {

  override def buildRequest(dataInput: Map[String, String]): Try[String] = Try {
    val filePath = dataInput.get("payloadFile")

    if (filePath.isEmpty) throw new IllegalArgumentException(s"payloadFile must be entered for ${getClass().getCanonicalName}")

    if (!new File(filePath.get).exists()) throw new IllegalArgumentException(s"payloadFile ${filePath} does not exist. Cannot proceed.")

    Source.fromFile(filePath.get).mkString
  }
}
