package org.ebayopensource.regression.internal.datastore

import org.ebayopensource.regression.internal.common.Util
import org.ebayopensource.regression.internal.components.recorder.common.RequestRecordingEntry

import scala.util.Try

/**
  * Created by asfernando on 4/18/17.
  */
abstract class BaseDataStore {

  val mapper = Util.getMapper()

  def storeStrategy(testIdentifier: String, strategyFileContent: String): Try[String] = Try {
    put(BaseDataStore.getStrategyContentKey(testIdentifier), strategyFileContent)
    "Added strategy file into datastore"
  }

  def getStrategy(testIdentifier: String): Option[String] = {
    get(BaseDataStore.getStrategyContentKey(testIdentifier)).orElse(None)
  }

  def deleteStrategy(testIdentifier:String) : Try[Unit] = Try {
    remove(BaseDataStore.getStrategyContentKey(testIdentifier))
  }

  def storeRequestRecording(testIdentifier: String, requestName: String, requestRecordingEntries: RequestRecordingEntry): Try[String] = Try {
    val recordingString = mapper.writeValueAsString(requestRecordingEntries)
    put(BaseDataStore.getRequestRecordingKey(testIdentifier, requestName), recordingString)
    testIdentifier
  }

  def getRequestRecording(testIdentifier: String, requestName: String): Try[RequestRecordingEntry] = Try {
    val recording = get(BaseDataStore.getRequestRecordingKey(testIdentifier, requestName))
    if (recording.isEmpty) {
      throw new IllegalStateException(s"Request ${requestName} is not present in datastore. " +
        s"Data store might lack integrity. Please erase the datastore and start again.")
    }
    mapper.readValue[RequestRecordingEntry](recording.get)
  }

  def deleteRecording(testIdentifier:String) : Try[Unit] = Try {
    deleteStrategy(testIdentifier)
    deleteRecordingFiles(testIdentifier)
  }

  def listStrategies(): Seq[String]
  def put(key: String, value: String)
  def get(key: String): Option[String]
  def remove(key: String)
  def close()
  def deleteRecordingFiles(testIdentifier: String) : Try[Unit]
}

object BaseDataStore {

  val strategyPrefix = "regressr."

  def getStrategyContentKey(testIdentifier: String) : String = s"${strategyPrefix}${testIdentifier}.strategy"

  def getRequestRecordingKey(testIdentifier: String, requestName: String) : String = s"${strategyPrefix}${testIdentifier}.${requestName}"

}