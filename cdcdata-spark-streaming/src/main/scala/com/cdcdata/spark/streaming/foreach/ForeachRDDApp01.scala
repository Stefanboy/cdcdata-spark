package com.cdcdata.spark.streaming.foreach

import org.apache.spark.SparkConf
import org.apache.spark.internal.Logging
import org.apache.spark.streaming.{Seconds, StreamingContext}
import scalikejdbc.{DB, SQL}
import scalikejdbc.config.DBs

/**
 * 该streaming接收 nc -lk 9998 发送的数据
 * 适用foreachRDD算子 将wc结果输出到mysql数据库
 */
object ForeachRDDApp01 extends Logging{
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName(this.getClass.getName)
    val ssc = new StreamingContext(sparkConf, Seconds(5))
    val stream = ssc.socketTextStream("jd", 9998)


    val result = stream.flatMap(_.split(","))
      .map((_, 1)).reduceByKey(_ + _)

    /**
     * streaming只要是写东西出去，只能是foreachRDD
     * 第一种方式会遇到闭包的问题
     * 只有算子是在executor端执行的 其他的都是在driver端执行 那么变量从driver向executor端传递 变量必须要进行序列化 但是如果变量不能进行序列化就会出现闭包问题
     * connection不能被序列化 所以会出现闭包的问题
     */
  /*  result.foreachRDD(rdd => {
      val connection = MySQLUtils.getConnection()
      rdd.foreach(pair => {
        val sql = s"insert into wc(word,cnt) values ('${pair._1}',${pair._2}"
        connection.createStatement().execute(sql)
      })
      MySQLUtils.closeConnection(connection)


    })*/
    /**
     * connection写到foreachPartition算子里了 所以是在executor端执行
     * 也就不会存在网络传输的问题  也不会出现闭包问题
     * 想实时处理 会对接kafka 分区数跟kafka的分区数一样 所以分区数不会太多
     */
 /*   result.foreachRDD(rdd => {
      rdd.foreachPartition(partition => {
        val connection = MySQLUtils.getConnection()
        //主要是看有几次连接
        logError("......")
        partition.foreach(pair => {
          val sql = s"insert into wc(word,cnt) values ('${pair._1}',${pair._2}"
          connection.createStatement().execute(sql)
        })
        MySQLUtils.closeConnection(connection)
      })


    })*/

    /**
     * 最优的方式是使用scalike方式，在连接池里去取，用完就放回去 不需要频繁关闭
     */

    result.foreachRDD(rdd => {
      rdd.foreachPartition(partition => {
        DBs.setupAll()//解析配置文件
        //主要是看有几次连接
        logError("......")
        partition.foreach(pair => {
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
        DBs.closeAll()
      })
    })

    ssc.start()
    ssc.awaitTermination()
  }



}
