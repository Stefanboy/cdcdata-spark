package com.cdcdata.spark.streaming.state

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * 让stream有状态
 * 之前没有状态的话只能对进来的这一批次做操作 有状态的话可以对该批次和以前的批次做操作
 * 比如求当天到现在为止的热点新闻
 *
 * 该版本在程序重新启动时会读不到原来状态的信息，因为重新启动后ssc是新的 当然取不到了。。。 所以改进02版本
 */
object StateApp01 {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName(this.getClass.getName)
    val ssc = new StreamingContext(sparkConf, Seconds(5))
    val stream = ssc.socketTextStream("jd", 9998)
    ssc.checkpoint("cdcdata-spark-streaming/state/chk")

    stream.flatMap(_.split(","))
        .map((_,1)).updateStateByKey(updateFunction)
        .print()
    ssc.start()
    ssc.awaitTermination()
  }

  //有可能当前批次的key在之前批次没有 所以getOrElse
  //newValues：当前批次的value组成的集合 preValues：之前批次的value组成的集合
  def updateFunction(newValues: Seq[Int], preValues: Option[Int]): Option[Int] = {
    val curr = newValues.sum
    val pre = preValues.getOrElse(0)
    Some(curr+pre)
  }


}
