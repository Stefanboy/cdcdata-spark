package com.cdcdata.spark.wc

import com.cdcdata.spark.utils.FileUtils
import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.LoggerFactory

object SparkWC {
  val log = LoggerFactory.getLogger(this.getClass)
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf()//.setMaster("local[2]").setAppName("")
    val sc = new SparkContext(conf)
    log.info("aaa")
    val (input,output) = (args(0),args(1))
    FileUtils.delete(sc.hadoopConfiguration,output)
    sc.textFile(input)
      .flatMap(_.split("\t"))
      .map((_,1))
      .reduceByKey(_+_)
      .saveAsTextFile(output)


    sc.stop()
  }

}
