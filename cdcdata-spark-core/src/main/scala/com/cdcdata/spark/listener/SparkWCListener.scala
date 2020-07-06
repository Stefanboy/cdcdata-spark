package com.cdcdata.spark.listener

import com.cdcdata.spark.utils.FileUtils
import org.apache.spark.{SparkConf, SparkContext}

object SparkWCListener {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf()
      .setMaster("local[2]")
      .set("spark.extraListeners","com.cdcdata.spark.listener.CdcdataSparkListener")//添加自定义监听器
      //设置自定义参数，需要spark.开头
      //如果运行在服务器，设置自定义参数 用--conf设置 k=v
      .set("spark.send.mail.enabled","true")
      .setAppName(this.getClass.getSimpleName)
    val sc = new SparkContext(conf)
    sc.parallelize(List("p,p,p","j,j","xing"))
      .flatMap(_.split(","))
      .map((_,1))
      .reduceByKey(_+_)
      .collect().foreach(println)


    sc.stop()
  }

}
