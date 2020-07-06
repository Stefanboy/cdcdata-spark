package com.cdcdata.spark.streaming.kafka

import java.util

import com.cdcdata.spark.streaming.offset.RedisOffsetsManager
import com.cdcdata.utils.db.RedisUtils
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.internal.Logging
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, KafkaUtils}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import redis.clients.jedis.{Jedis, Pipeline}

/**
 * streaming对接kafka
 * 使用redis管理offset 封装版本
 */
object StreamingKafkaApp05 extends Logging{
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName(this.getClass.getName)
    val ssc = new StreamingContext(sparkConf, Seconds(5))

    val groupId = "ccdcdata_group"
    val kafkaParams = Map[String, Object](
      //kafka的地址
      "bootstrap.servers" -> "jd:9092,jd:9093,jd:9094",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> groupId,
      //从最早的开始消费
      "auto.offset.reset" -> "earliest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    //topic的名字
    val topics = Array("cdcdata3partstopic")
    //TODO...去offsets存储的地方取offsets
    //因为消费方式是earliest 所以去offset存储的地方获取到已有的offset 避免重复消费
    val fromOffsets = RedisOffsetsManager.obtainOffsets(topics,groupId)

    //点进Subscribe源码第230行 看到offsets 拿到这个参数的类型，在进程序之前从redis拿到offset
    // 然后组装好 fromOffsets 这样就能从指定offset进行消费了
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams,fromOffsets)
    )

    stream.foreachRDD(rdd => {

      if (!rdd.isEmpty()) {

        // 这里的rdd必须是KafkaRDD 如果不是KafkaRDD就无法转换OffsetRanges
        // 要拿Kafka+SS对接过来的最原始的stream
        //拿到offsetRanges后再做业务操作
        //将rdd转换成OffsetRange 返回一个offset数组 进入源码 89行看到 第3、4个参数 从哪开始 到哪结束 这就是偏移量
        val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
        //打印出offset数据的信息
        offsetRanges.foreach(o => {
          println(s"${o.topic} ${o.partition} ${o.fromOffset} ${o.untilOffset}")
        })

        //TODO...业务逻辑处理
        //val result = rdd.flatMap(x => x.value()).map((_, 1)).reduceByKey(_ + _).collect()
        val result = rdd.map(x => (x.value(), 1)).reduceByKey(_ + _).collect()
        var jedis: Jedis = null
        var pipeline: Pipeline = null

        try {
          //redis中是没有事物的 但是有Pipeline操作 该操作可以保证事物
          jedis = RedisUtils.getJedis
          pipeline = jedis.pipelined()
          pipeline.multi()

          //TODO 1...存计算结果
          for (pair <- result) {
            pipeline.hincrBy("wc_redis",pair._1,pair._2)
          }

          //TODO 2...提交offset

          RedisOffsetsManager.storeOffsets(offsetRanges,groupId)
          pipeline.exec()
          pipeline.sync()

        } catch {
          case e:Exception =>{
            pipeline.discard()
            e.printStackTrace()
          }
        } finally {
          pipeline.close()
          jedis.close()

        }
      } else {
        println("当前批次没有数据")
      }
    })

    ssc.start()
    ssc.awaitTermination()
  }



}
