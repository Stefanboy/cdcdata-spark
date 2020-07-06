package com.cdcdata.spark.streaming.kafka

import com.cdcdata.spark.streaming.foreach.MySQLUtils
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.{SparkConf, TaskContext}
import org.apache.spark.internal.Logging
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, KafkaUtils, OffsetRange}
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * streaming对接kafka
 * 管理偏移量
 */
object StreamingKafkaApp02 extends Logging{
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName(this.getClass.getName)
    val ssc = new StreamingContext(sparkConf, Seconds(5))

    val kafkaParams = Map[String, Object](
      //kafka的地址
      "bootstrap.servers" -> "jd:9092,jd:9093,jd:9094",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "ccdcdata_group",
      //从最早的开始消费
      "auto.offset.reset" -> "earliest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    //topic的名字
    val topics = Array("cdcdata3partstopic")
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    stream.foreachRDD(rdd => {
      logError("..."+rdd.partitions.size)
      //将rdd转换成OffsetRange 返回一个offset数组 进入源码 89行看到 第3、4个参数 从哪开始 到哪结束 这就是偏移量
      val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      //打印出offser数据的信息
      offsetRanges.foreach(o => {
        println(s"${o.topic} ${o.partition} ${o.fromOffset} ${o.untilOffset}")
      })

      //TODO...业务逻辑处理
      logError(rdd.count()+"......")
      //TODO...提交offset

    })



    ssc.start()
    ssc.awaitTermination()
  }



}
