package com.cdcdata.spark.work

import com.cdcdata.spark.utils.FileUtils
import org.apache.spark.{SparkConf, SparkContext}

/**
 * 监控参数写入到MySQL：读进来多少数据，shuffle了多少数据，写出去多少数据 的主类
 */
object WCListener2MySQL {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf()
      .setMaster("local[2]")
      .set("spark.extraListeners","com.cdcdata.spark.work.CdcListenerTask")//添加自定义监听器
      //设置自定义参数，需要spark.开头
      //如果运行在服务器，设置自定义参数 用--conf设置 k=v
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
