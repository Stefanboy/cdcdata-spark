package com.cdcdata.spark.streaming.work

import java.sql.{Connection, DriverManager, PreparedStatement}

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.{Seconds, StreamingContext}

object StreamingKafkaMySQL {

  def main(args: Array[String]): Unit = {
    //创建SparkStreaming入口
    val conf = new SparkConf().setMaster("local[2]").setAppName("JDBCOffsetApp")
    val ssc = new StreamingContext(conf,Seconds(5))
    val topics = Array("cdcdata3partstopic")
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

    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    stream.foreachRDD(rdd=>{
      if(!rdd.isEmpty()){
        //Class.forName("com.mysql.jdbc.Driver")
        //输出rdd的数据量
        println("数据统计记录为："+rdd.count())
        //val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
        var pstmt: PreparedStatement = null
        var connection: Connection = null
        var catagory = ""
        val mapRDD = rdd.map(x => {
          val split = x.value().split(",")
          catagory = split(3)
          split
        })
        try{
          mapRDD.foreachPartition(partition =>{
            connection = DriverManager.getConnection("jdbc:mysql//jd:3306/bigdata", "root", "mysqladmin")
            connection.setAutoCommit(false)
            val pstmt = connection.prepareStatement(s"insert into order_${catagory}(orderid,money,catagory) values (?,?,?)")
            partition.foreach(x => {
              pstmt.setInt(1, x(0).toInt)
              pstmt.setString(2, x(1))
              pstmt.setString(3, x(2))
              pstmt.addBatch()
            })
            //批量提交
            pstmt.executeBatch()
            connection.commit()
            //connection.close()
          })

        } catch {
          case e:Exception =>e.printStackTrace()
        } finally {
          if(pstmt != null) pstmt.close()
          if(connection != null) connection.close()

        }
      }else {
        println("当前批次没有数据")
      }
    })

    ssc.start()
    ssc.awaitTermination()
  }
}
