package com.cdcdata.spark.streaming.basic

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * streaming WC程序
 * 运行流程 启动监控端口
 * 云主机 向端口中写数据 nc -lk 9998
 * 地址：http://spark.apache.org/docs/latest/streaming-programming-guide.html
 * A Quick Example
 */
object StreamingWCApp {
  def main(args: Array[String]): Unit = {
    //如果使用socket方式监控setMaster至少为2 因为receiver需要占用一个core一致不间断的接收数据
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName(this.getClass.getName)
    //创建ssc 每5s监控一批数据
    val ssc = new StreamingContext(sparkConf, Seconds(5))
    //TODO... 添加业务逻辑
    val stream: ReceiverInputDStream[String] = ssc.socketTextStream("jd", 9998)
    stream.flatMap(_.split(","))
        .map((_,1))
        .reduceByKey(_+_)
        .print()
    ssc.start()//启动
    ssc.awaitTermination()//一直监控
  }

}
