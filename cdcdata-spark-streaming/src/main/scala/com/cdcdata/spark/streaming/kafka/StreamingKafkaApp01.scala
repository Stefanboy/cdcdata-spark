package com.cdcdata.spark.streaming.kafka

import com.cdcdata.spark.streaming.foreach.ForeachRDDApp01.logError
import com.cdcdata.spark.streaming.foreach.MySQLUtils
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.internal.Logging
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import scalikejdbc.{DB, SQL}
import scalikejdbc.config.DBs

/**
 * streaming对接kafka
 * 消费kafka数据
 */
object StreamingKafkaApp01 extends Logging{
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
    //去kafka数据的第2位 即value
    stream.map(record => (record.key, record.value)).map(_._2).print()
    //查看总共消费的多少数据
    stream.map(record => (record.key, record.value)).map(_._2).count().print()
    //wc程序写入mysql数据库
    stream.map(record => (record.key, record.value))
      .map(_._2)
      .flatMap(_.split(","))
      .map((_,1))
      .reduceByKey(_+_)
      .foreachRDD(rdd => {
        rdd.foreachPartition(partition => {
          //val connection = MySQLUtils.getConnection()
          DBs.setupAll()//解析配置文件
          //主要是看有几次连接
          logError("......")
          partition.foreach(pair => {
            /*val sql = s"insert into wc(word,cnt) values ('${pair._1}',${pair._2}"
            connection.createStatement().execute(sql)*/
            DB.autoCommit({
              implicit session =>{
                val sql = s"insert into wc(word,cnt) values ('${pair._1}',${pair._2}"
                SQL("insert into wc(word,cnt) values (?,?)")
                  .bind(pair._1,pair._2)
                  .update()
                  .apply()
              }

            })
          })
          //MySQLUtils.closeConnection(connection)
          DBs.closeAll()
        })


      })


    ssc.start()
    ssc.awaitTermination()
  }



}
