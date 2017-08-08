package org.ebayopensource.regression.internal.reader

import java.net.URI
import java.util
import java.util.{UUID, List => JList, Map => JMap}

import org.ebayopensource.regression.internal.components.comparator.common.Comparator
import org.ebayopensource.regression.internal.components.continuation.Continuation
import org.ebayopensource.regression.internal.components.printer.ProgressPrinter
import org.ebayopensource.regression.internal.components.recorder.common.Recorder
import org.ebayopensource.regression.internal.components.requestBuilder.RequestPayloadBuilder
import com.fasterxml.jackson.annotation.JsonProperty

import scala.collection.JavaConversions._

/**
  * Created by asfernando on 4/17/17.
  */
class TestStrategy(@JsonProperty("service") _service: Service,
               @JsonProperty(value = "commonHeaders", required = false) _headers: JMap[String, String],
               @JsonProperty("requests") _requests: JList[RequestEntry]) {
  val service = _service
  val headers: Map[String, String] = setSpecialHeaders(Option(_headers).getOrElse(new util.HashMap).toMap)
  val requests = _requests.toList

  def setSpecialHeaders(headers: Map[String, String]): Map[String, String] = {
    headers map { case (k,v) => if (v.equals("<new-guid>")) (k, UUID.randomUUID().toString) else (k, v) }
  }
}

class Service(@JsonProperty(value = "baseURL", required = true) _baseURL: String) {
  val baseURL = new URI(_baseURL)
}

class RequestEntry(@JsonProperty(value = "requestName", required = true) _reqName: String,
                   @JsonProperty(value = "path", required = true) _path: String,
                   @JsonProperty(value = "method", required = true) _method: String,
                   @JsonProperty(value = "extraHeaders", required = false) _headers: JMap[String, String],
                   @JsonProperty(value = "requestBuilder", required = false) _bodyBuilder: String,
                   @JsonProperty(value = "recorder", required = true) _recorder: String,
                   @JsonProperty(value = "continuation", required = false) _continuation: String,
                   @JsonProperty(value = "comparator", required = true) _comparator: String,
                   @JsonProperty(value = "dataInput", required = false) _dataInput: JMap[String, String],
                   @JsonProperty(value = "progressPrinter", required = false) _progressPrinter: String)
{
  val requestName = _reqName.replaceAll(" ", "_")
  val path = _path
  val method : HttpMethods.Value = HttpMethods.withName(_method)
  val extraHeaders = Option(_headers).getOrElse(new util.HashMap[String, String]()).toMap
  val requestBuilder = if (Option(_bodyBuilder).isDefined) Some(Class.forName(_bodyBuilder).newInstance().asInstanceOf[RequestPayloadBuilder]) else None
  val recorder = Class.forName(_recorder).newInstance().asInstanceOf[Recorder]
  val comparator = Class.forName(_comparator).newInstance().asInstanceOf[Comparator]
  val continuation = if (Option(_continuation).isDefined) Some(Class.forName(_continuation).newInstance().asInstanceOf[Continuation]) else None
  val dataInput = Option(_dataInput).getOrElse(new util.HashMap[String, String]()).toMap
  val progressPrinter = if (Option(_progressPrinter).isDefined) Some(Class.forName(_progressPrinter).newInstance().asInstanceOf[ProgressPrinter]) else None
}

object HttpMethods extends Enumeration {
  val GET, POST, PUT, DELETE = Value
}
