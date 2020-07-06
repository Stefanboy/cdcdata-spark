package com.cdcdata.spark.work

import java.text.SimpleDateFormat

import com.cdcdata.spark.utils.{ETLUtils, FileUtils}
import org.apache.spark.{SparkConf, SparkContext}
import org.lionsoul.ip2region.{DbConfig, DbSearcher}

/**
 * mr的etl改为spark
 */
object ETLSparkV2 {


  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()//.setAppName("mySpark").setMaster("local")
    val sc = new SparkContext(conf)
    val (input,output) = (args(0),args(1))
    FileUtils.delete(sc.hadoopConfiguration,output)
    sc.textFile(input)
        .map(x =>{
          val config = new DbConfig
          val path = this.getClass.getResource("/ip2region.db").getPath
          //val path = args(2)
          val searcher = new DbSearcher(config, path)
          val format = new SimpleDateFormat("[dd/MM/yyyy:HH:mm:ss +0800]")
          ETLUtils.logParser(x,searcher,format)
        }).filter(_.responseSize != "-").map(_.toString).saveAsTextFile(output)
    sc.stop()
  }

}
