package com.cdcdata.spark.listener

import com.cdcdata.spark.utils.MsgUtils
import org.apache.spark.SparkConf
import org.apache.spark.internal.Logging
import org.apache.spark.scheduler.{SparkListener, SparkListenerTaskEnd}
import org.json4s.DefaultFormats
import org.json4s.jackson.Json

import scala.collection.mutable

/**
 * CdcdataSparkListener是个监听器，不需要运行，整合到代码中去就自动监听
 * 如何整合呢 在主运行程序中添加
 *
 */
class CdcdataSparkListener(conf: SparkConf) extends SparkListener with Logging{

  override def onTaskEnd(taskEnd: SparkListenerTaskEnd): Unit = {
    logError("~~~~~~~~~~~enter onTaskEnd~~~~~~~~~~~~~~~~")
    // TODO... 能够拿到很多信息，把信息都持久化到HBase/Redis/RDBMS

    // TODO... 如何拿到Spark作业的名字?  sparkConf
    val appName = conf.get("spark.app.name")
    //拿到的是  task的所有信息 也就是说该方法在每一个task都执行一次
    val metrics = taskEnd.taskMetrics
    //想要什么信息，在taskMetrics中通过方法取就可以了
    val taskMetricsMap = mutable.HashMap(
      "executorDeserializeTime" -> metrics.executorDeserializeTime,
      "executorDeserializeCpuTime" -> metrics.executorDeserializeCpuTime,
      "executorRunTime" -> metrics.executorCpuTime
    )
//    logError(appName)
//      logError(metrics.toString)
    logError(Json(DefaultFormats).write(taskMetricsMap)) //map转json

//    logError(conf.get("spark.send.mail.enabled"))
    //有些任务超标，需要告警，比如说发邮件
    //发邮件过程肯定是异步的，需要改进
    //自定义参数控制是否发邮件，
    /*if ("true" == conf.get("spark.send.mail.enabled")) {
      MsgUtils.send("桶子",s"$appName _task", Json(DefaultFormats).write(taskMetricsMap))
    }*/
  }
}
