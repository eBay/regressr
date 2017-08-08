package org.ebayopensource.regression.internal.components.requestBuilder

import java.io.{File, PrintWriter}

import org.ebayopensource.regression.UnitSpec

/**
  * Created by asfernando on 5/16/17.
  */
class FileHTTPBodyBuilderTest extends UnitSpec {

  "A file http request builder" should "be able to read content from a file path" in {
    val pw = new PrintWriter(new File("/tmp/tmp.txt"))
    pw.write("text")
    pw.flush()
    pw.close()

    assert(new FileHTTPBodyBuilder().buildRequest(Map("payloadFile" -> "/tmp/tmp.txt")).get == "text")

    new File("/tmp/tmp.txt").delete()
  }

  "A file http request builder" should "throw exception when file was not found" in {
    assertThrows[IllegalArgumentException](new FileHTTPBodyBuilder().buildRequest(Map("payloadFile" -> "/tmp/tmp.txt")).get == "text")
  }

  "A file http request builder" should "throw exception when file path was not found" in {
    assertThrows[IllegalArgumentException](new FileHTTPBodyBuilder().buildRequest(Map()).get == "text")
  }
}
