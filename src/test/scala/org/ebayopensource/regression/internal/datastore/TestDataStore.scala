package org.ebayopensource.regression.internal.datastore
import scala.util.Try

/**
  * Created by asfernando on 5/16/17.
  */
class TestDataStore(strategiesLister: Option[()=>Seq[String]], recordingDeleter: Option[(String) => Try[Unit]]) extends BaseDataStore {

  val map = scala.collection.mutable.HashMap[String, String]()

  override def listStrategies(): Seq[String] = if (strategiesLister.isDefined) strategiesLister.get() else Seq()

  override def put(key: String, value: String): Unit = {
    map.put(key, value)
  }

  override def get(key: String): Option[String] = map.get(key)

  override def remove(key: String): Unit = map.remove(key)

  override def close(): Unit = ???

  override def deleteRecordingFiles(testIdentifier: String): Try[Unit] = ???
}
