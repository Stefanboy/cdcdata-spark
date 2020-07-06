package com.cdcdata.spark.work

import java.sql.DriverManager

import org.apache.spark.SparkConf
import org.apache.spark.internal.Logging
import org.apache.spark.scheduler.{SparkListener, SparkListenerTaskEnd}
import org.json4s.DefaultFormats
import org.json4s.jackson.Json

import scala.collection.mutable

/**
 *
 * 监控参数写入到MySQL：读进来多少数据，shuffle了多少数据，写出去多少数据
 *
 */
class CdcListenerTask(conf: SparkConf) extends SparkListener with Logging{

  override def onTaskEnd(taskEnd: SparkListenerTaskEnd): Unit = {
    logError("~~~~~~~~~~~enter onTaskEnd~~~~~~~~~~~~~~~~")
    // TODO... 能够拿到很多信息，把信息都持久化到HBase/Redis/RDBMS

    // TODO... 如何拿到Spark作业的名字?  sparkConf
    val appName = conf.get("spark.app.name")
    //拿到的是  task的所有信息 也就是说该方法在每一个task都执行一次
    val metrics = taskEnd.taskMetrics
    val inputMetrics = metrics.inputMetrics
    val outputMetrics = metrics.outputMetrics
    val shuffleReadMetrics = metrics.shuffleReadMetrics
    val shuffleWriteMetrics = metrics.shuffleWriteMetrics

    //想要什么信息，在taskMetrics中通过方法取就可以了
    val taskMetricsMap = mutable.HashMap(
      "inputRecordsRead" -> inputMetrics.recordsRead,
      "inputBytesRead" -> inputMetrics.bytesRead,
      "outputBytesWritten" -> outputMetrics.bytesWritten,
      "outputRecordsWritten" -> outputMetrics.recordsWritten,
      "shuffleReadTotalBytesRead" -> shuffleReadMetrics.totalBytesRead,
      "shuffleReadRecordsRead" -> shuffleReadMetrics.recordsRead,
      "shuffleWriteBytesWritten" -> shuffleWriteMetrics.bytesWritten,
      "shuffleWriteRecordsWritten" -> shuffleWriteMetrics.recordsWritten
    )

    val result = Json(DefaultFormats).write(taskMetricsMap).toString //map转json
    JDBCUtils(result).saveMySQL
  }
  case class JDBCUtils(result:String) {
    val connection = DriverManager.getConnection("jdbc:mysql://jd:3306/bigdata", "root", "mysqladmin")

    def saveMySQL(): Unit = {

      println("-----------------------------")
      val pstmt = connection.prepareStatement("insert into t_listen (data) values (?)")
      pstmt.setString(1, result)
      pstmt.execute()

    }
    //connection.close()



  }




  /*override def onStageCompleted(stageCompleted: SparkListenerStageCompleted): Unit = {
    logError("~~~~~~~~~~~enter onTaskEnd~~~~~~~~~~~~~~~~")
    // TODO... 能够拿到很多信息，把信息都持久化到HBase/Redis/RDBMS

    // TODO... 如何拿到Spark作业的名字?  sparkConf
    val appName = conf.get("spark.app.name")
    //拿到的是  task的所有信息 也就是说该方法在每一个task都执行一次
    val info: StageInfo = stageCompleted.stageInfo
    val metrics = info.taskMetrics
    //val metrics = taskEnd.taskMetrics
    val inputMetrics = metrics.inputMetrics
    val outputMetrics = metrics.outputMetrics
    val shuffleReadMetrics = metrics.shuffleReadMetrics
    val shuffleWriteMetrics = metrics.shuffleWriteMetrics

    //想要什么信息，在taskMetrics中通过方法取就可以了
    val taskMetricsMap = mutable.HashMap(
      "inputRecordsRead" -> inputMetrics.recordsRead,
      "inputBytesRead" -> inputMetrics.bytesRead,
      "outputBytesWritten" -> outputMetrics.bytesWritten,
      "outputRecordsWritten" -> outputMetrics.recordsWritten,
      "shuffleBytesWritten" -> shuffleWriteMetrics.bytesWritten,
      "shuffleRecordsWritten" -> shuffleWriteMetrics.recordsWritten,
    )
    //    logError(appName)
    //      logError(metrics.toString)
    logError(Json(DefaultFormats).write(taskMetricsMap)) //map转json

    //    logError(conf.get("spark.send.mail.enabled"))
  }*/
}
