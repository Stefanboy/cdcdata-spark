package com.cdcdata.spark.work

import com.cdcdata.spark.utils.FileUtils
import org.apache.spark.{SparkConf, SparkContext}

/**
 * spark 实现etl后数据的省份求和
 */
object ETLSparkCountProvinceV2 {


  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()//.setAppName("mySpark").setMaster("local")
    val sc = new SparkContext(conf)
    val (input,output) = (args(0),args(1))
    FileUtils.delete(sc.hadoopConfiguration,output)
    sc.textFile(input).map(x =>{
      val split = x.split("\t")
      (split(10),split(8).toInt)
    }).reduceByKey(_+_).map(x => x._1+"\t"+x._2).saveAsTextFile(output)

    sc.stop()
  }

}
