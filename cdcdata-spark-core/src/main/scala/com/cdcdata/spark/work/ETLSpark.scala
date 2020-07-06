package com.cdcdata.spark.work

import java.text.SimpleDateFormat

import com.cdcdata.spark.utils.{ETLUtils, FileUtils}
import org.apache.spark.{SparkConf, SparkContext}
import org.lionsoul.ip2region.{DbConfig, DbSearcher}
import org.slf4j.LoggerFactory

/**
 * sparkcore进行离线项目etl数据清洗
 */
object ETLSpark {
  private val log = LoggerFactory.getLogger(this.getClass)


  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("mySpark").setMaster("local")
    val sc = new SparkContext(conf)
    log.warn("aaaaaa")
    FileUtils.delete(sc.hadoopConfiguration,"E:/bigdata/workSpace/cdcdata-spark/cdcdata-spark-core/out")
    sc.textFile("file:///E:/bigdata/workSpace/cdcdata-spark/cdcdata-spark-core/data/access.log")
        .map(x =>{
          val config = new DbConfig
          val path = this.getClass.getResource("/ip2region.db").getPath
          val searcher = new DbSearcher(config, path)
          val format = new SimpleDateFormat("[dd/MM/yyyy:HH:mm:ss +0800]")
          ETLUtils.logParser(x,searcher,format)
        }).filter(_.responseSize != "-").map(_.toString).saveAsTextFile("file:///E:/bigdata/workSpace/cdcdata-spark/cdcdata-spark-core/out")
    /*sc.textFile("file:///E:/bigdata/workSpace/cdcdata-spark/cdcdata-spark-core/out/part*").map(x =>{
      val split = x.split("\t")
      (split(10),split(8).toInt)
    }).reduceByKey(_+_).map(x => x._1+"\t"+x._2).saveAsTextFile("file:///E:/bigdata/workSpace/cdcdata-spark/cdcdata-spark-core/output")
*/

    sc.stop()
  }

}
//case class Access(userid:String,ip:String,proxyIp:String,responseTime:Long,referer:String,method:String,url:String,httpCode:String,requestSize:Long,responseSize:Long,cache:String,year:String,month:String,day:String,province:String,city:String,isp:String,http:String,domain:String,path:String)
