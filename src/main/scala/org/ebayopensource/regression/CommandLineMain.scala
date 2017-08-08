package org.ebayopensource.regression

import java.io.File

import org.ebayopensource.regression.internal.datastore.{BaseDataStore, FileDataStore}
import org.ebayopensource.regression.internal.http.SimpleHttpClient
import org.ebayopensource.regression.internal.reader.YAMLTestStrategyReader
import org.ebayopensource.regression.internal.reportGenerator.HTMLReportGenerator
import org.ebayopensource.regression.internal.workflow.{DeleteWorkflow, ListWorkflow, RecordWorkflow, ReplayWorkflow}
import org.slf4j.LoggerFactory

import scala.io.Source
import scala.util.{Failure, Success}
import scalaj.http.BaseHttp

/**
  * Created by asfernando on 4/19/17.
  */
object CommandLineMain {

  val text = "The l33t service tester"

  val logger = LoggerFactory.getLogger(CommandLineMain.getClass)

  val graphic = " _______  _______  _______  _______  _______  _______  _______  _______ \n(  ____ )" +
    "(  ____ \\(  ____ \\(  ____ )(  ____ \\(  ____ \\(  ____ \\(  ____ )\n| (    )|| (    \\/| (    \\/| (    )||" +
    " (    \\/| (    \\/| (    \\/| (    )|\n| (____)|| (__    | |      | (____)|| (__    | (_____ | (_____ | (____)|" +
    "\n|     __)|  __)   | | ____ |     __)|  __)   (_____  )(_____  )|     __)\n| (\\ (   | (   " +
    "   | | \\_  )| (\\ (   | (            ) |      ) || (\\ (   \n| ) \\ \\__| (____/\\| (___) || ) \\ \\__| " +
    "(____/\\/\\____) |/\\____) || ) \\ \\__\n|/   \\__/(_______/(_______)|/   \\__/(_______/\\_______)\\" +
    "_______)|/   \\__/\n                                                                        " + text + " \n"

  val version = "0.1.SNAPSHOT \n"

  val recordCmd = "record"
  val replayCmd = "compareWith"
  val listCmd = "list"
  val deleteCmd = "delete"

  val usage = s"\n\nUsage: regressr COMMAND PARAMETERS \n \n" +
    "COMMANDS: \n \n" +
          s"${recordCmd} RELEASE STRATEGY-FILE          Records the current state using a specified strategy \n" +
          s"${replayCmd} [RELEASEs]                Compares the current state with a specified strategy \n" +
          s"${listCmd}                                  Lists the stored recordings \n" +
          s"${deleteCmd} [RELEASEs]                     Deletes the specified recording"


  def main(args: Array[String]) = {
    val dataStore = new FileDataStore(FileDataStore.PATH)
    isValidInput(args) match {
      case true => {
        args(0) match {
          case this.recordCmd => performRecord (args, dataStore)
          case this.replayCmd => performReplay (dataStore, args)
          case this.listCmd => logger.info (new ListWorkflow (dataStore).listWorkflows ().mkString (", ") )
          case this.deleteCmd => new DeleteWorkflow (dataStore).delete (args.slice (1, args.length) )
            .foreach (e => logger.info (s"Recording ${e} is deleted") )
        }
        System.exit(0)
      }
      case false => println(usageString)
    }
  }

  def performReplay(dataStore: BaseDataStore, args: Array[String]): Integer = {
    val recordings = args.slice(1, args.length)
    val storedRecordings = dataStore.listStrategies()
    val notPresent = recordings.filter {
      recordingName => !storedRecordings.contains(recordingName)
    }
    if (notPresent.isEmpty) {
      val replayOutput = new ReplayWorkflow(dataStore,
        new SimpleHttpClient(new BaseHttp()), new HTMLReportGenerator()).replayAndFetchReport(recordings)
      logger.info(replayOutput.message)
      replayOutput.noOfRegressions
    }
    else {
      logger.error(s"The strategies ${notPresent.mkString(",")} were not recorded. Use ${listCmd} to view recorded strategies")
      0
    }
  }

  def performRecord(args: Array[String], dataStore: FileDataStore) = {
    if (!new File(args(2)).exists()) {
      logger.error(s"Cannot record. File ${args(2)} not found.")
    }
    else if (dataStore.listStrategies().contains(args(1))) {
      logger.error(s"There is already a strategy that is recorded with the name of ${args(1)}. Delete the earlier recording.")
    }
    else {
      val strategyFile = new File(args(2))
      val testIdentifier = args(1)
      YAMLTestStrategyReader.read(Source.fromFile(strategyFile).mkString.replace("\t","")) match {
        case Success(strategy) => {
          new RecordWorkflow(dataStore, new SimpleHttpClient(new BaseHttp())).recordState(testIdentifier, strategy) match {
            case Success(_) => {
              dataStore.storeStrategy(args(1), Source.fromFile(strategyFile.toString).mkString)
              logger.info(s"$testIdentifier was recorded successfully.")
            }
            case Failure(f) => logger.error(f.getMessage(), f)
          }
        }
        case Failure(t) => {
          throw new IllegalStateException(s"Failed to read test:= $testIdentifier from location $strategyFile. Reason is:= $t")
        }
      }
    }
  }

  def usageString() = graphic + version + usage

  def isValidInput(args: Array[String]): Boolean = {
    (args.length>0) match {
      case true => {
        val cmd = args(0)
        ( cmd.equals(recordCmd) && (args.length==3) )  ||
          (cmd.equals(replayCmd) && (args.length>=2) ) ||
            (cmd.equals(listCmd) && args.length==1)    ||
          (cmd.equals(deleteCmd) && args.length>=2)

      }
      case false => false
    }
  }
}
