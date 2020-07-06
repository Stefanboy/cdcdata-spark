package com.cdcdata.spark.streaming.kafka

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.internal.Logging
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * streaming对接kafka 消费kafka数据
 * yarn运行
 *
 * jd:9092,jd:9093,jd:9094 ccdcdata_group cdcdata3partstopic
 */
object StreamingKafkaSQLApp extends Logging{
  def main(args: Array[String]): Unit = {
    //在外面传递brokers topics groupId
    /*if(args.length != 3){
      System.err.println("Usage: StreamingKafkaYARNApp <brokers> <topics> <groupid>")
      System.exit(-1)
    }*/

    val sparkConf = new SparkConf()
      .setMaster("local[2]")
      .setAppName(this.getClass.getName)
    val ssc = new StreamingContext(sparkConf, Seconds(5))
    //use_a_separate_group_id_for_each_stream
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
    //topic的名字 cdcdata3partstopic
    val topics = Array("cdcdata3partstopic")
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )
    //wc程序写入mysql数据库

    //result.print()
    stream.map(_.value())
      .foreachRDD(rdd => {
      val spark = SparkSession.builder.config(rdd.sparkContext.getConf).getOrCreate()
      import spark.implicits._
      val wordsDataFrame = rdd.toDF("word")
        wordsDataFrame.groupBy("word").count().show()
    })
    ssc.start()
    ssc.awaitTermination()
  }



}
