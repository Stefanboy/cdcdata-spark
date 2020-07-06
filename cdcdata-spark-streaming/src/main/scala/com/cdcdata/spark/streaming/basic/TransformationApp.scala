package com.cdcdata.spark.streaming.basic

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * /**
 * * streaming Transformation程序
 *运行流程 启动监控端口
 *云主机 向端口中写数据 nc -lk 9998
 *地址：http://spark.apache.org/docs/latest/streaming-programming-guide.html
 *
 **/
 */
object TransformationApp {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[2]").setAppName(this.getClass.getName)
    val ssc = new StreamingContext(conf, Seconds(5))
    val stream: ReceiverInputDStream[String] = ssc.socketTextStream("jd", 9998)
    //求输入了多少行数据
    stream.count().print()
    //求元素的数量
    stream.flatMap(_.split(",")).count().print()
    //求相同key出现的次数
    stream.flatMap(_.split(",")).countByValue().print()

    ssc.start()
    ssc.awaitTermination()

  }

}
