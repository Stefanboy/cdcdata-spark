package com.cdcdata.spark.streaming.state

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * 让stream有状态
 * 之前没有状态的话只能对进来的这一批次做操作 有状态的话可以对该批次和以前的批次做操作
 * 比如求当天到现在为止的热点新闻
 *
 * 该版本在程序重新启动时就能读到原来的批次信息了 但是自定义的函数里没有key 下一个版本改进
 */
object StateApp02 {
  def main(args: Array[String]): Unit = {


    val checkpoint = "cdcdata-spark-streaming/state/state-checkpoint"
    def functionToCreateContext:StreamingContext = {
      val sparkConf = new SparkConf().setMaster("local[2]").setAppName(this.getClass.getName)
      val ssc = new StreamingContext(sparkConf, Seconds(5))
      val stream = ssc.socketTextStream("jd", 9998)
      ssc.checkpoint(checkpoint)

      stream.flatMap(_.split(","))
        .map((_,1)).updateStateByKey(updateFunction)
        .print()
      ssc
    }

    val ssc = StreamingContext.getOrCreate(checkpoint, functionToCreateContext _)




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
