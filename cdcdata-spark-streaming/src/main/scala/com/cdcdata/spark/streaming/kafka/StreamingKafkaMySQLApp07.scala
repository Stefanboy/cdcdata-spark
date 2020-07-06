package com.cdcdata.spark.streaming.kafka

import com.cdcdata.spark.streaming.offset.MySQLOffsetsManager
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, KafkaUtils}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import scalikejdbc.config.DBs
import scalikejdbc.{DB, SQL}

object StreamingKafkaMySQLApp07 {

    def main(args: Array[String]): Unit = {
      //创建SparkStreaming入口
      val conf = new SparkConf().setMaster("local[2]").setAppName("JDBCOffsetApp")
      val ssc = new StreamingContext(conf,Seconds(5))
      val topics = Array("cdcdata3partstopic")
      val groupId = "ccdcdata_group"
      //kafka参数
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
      DBs.setup()
      val offsets = DB.readOnly( implicit session => {
        SQL("select * from kafka_offset").map(rs => {
          (Offset(rs.string("topic"),rs.string("groupid"),rs.int("partitions"),rs.long("untiloffset")))
        }).list().apply()
      })
      var fromOffsets = MySQLOffsetsManager.obtainOffsets(topics,groupId)

      val stream = KafkaUtils.createDirectStream[String, String](
        ssc,
        PreferConsistent,
        Subscribe[String, String](topics, kafkaParams,fromOffsets)
      )

      stream.foreachRDD(rdd=>{
        if(!rdd.isEmpty()){
          //输出rdd的数据量
          println("数据统计记录为："+rdd.count())
          val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
          MySQLOffsetsManager.storeOffsets(offsetRanges,groupId)
        }else {
          println("当前批次没有数据")
        }
      })

      ssc.start()
      ssc.awaitTermination()
    }

  case class Offset(topic:String,groupid:String,partitions:Int,untiloffset:Long)
}
