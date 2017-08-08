package org.ebayopensource.regression.internal.datastore

import java.io.{File, PrintWriter}

import scala.io.Source
import scala.util.Try

/**
  * Created by asfernando on 4/19/17.
  */
class FileDataStore(path: String) extends BaseDataStore {

  new File(path).mkdir()

  override def put(key: String, value: String): Unit = {
    val pw = new PrintWriter(new File(buildFilePath(key)))
    pw.write(value)
    pw.flush()
    pw.close()
  }

  private def buildFilePath(key: String) = {
    if (key.endsWith(".strategy")) s"${path}${key}" else s"${path}${key}.json"
  }

  override def get(key: String): Option[String] = {
    val file = new File(buildFilePath(key))
    if (!file.exists()) {
      None
    }
    else {
      Some(Source.fromFile(file).mkString)
    }
  }

  override def close(): Unit = ??? // This is a no op.

  override def listStrategies(): Seq[String] = {
    new File(path).listFiles().filter {
      file => file.getName.endsWith(".strategy")
    }.map(file => file.getName.replaceFirst(s"${BaseDataStore.strategyPrefix}", "").replaceFirst(".strategy", ""))
  }

  override def remove(key: String): Unit = {
    new File(s"${path}${key}").delete()
  }

  override def deleteRecordingFiles(testIdentifier: String): Try[Unit] = Try {
    new File(path).listFiles().filter {
      file => file.getName.startsWith(s"${BaseDataStore.strategyPrefix}${testIdentifier}.")
    }.map(file => file.delete())
  }
}

object FileDataStore {
  val PATH="./tmp/"
}
