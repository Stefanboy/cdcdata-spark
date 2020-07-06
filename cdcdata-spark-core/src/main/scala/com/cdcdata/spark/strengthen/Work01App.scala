package com.cdcdata.spark.strengthen

import org.apache.spark.{SparkConf, SparkContext}

/**
 * 需求
 * a,1,3
 * a,2,4
 * b,1,1
 * ==>
 * a,3,7
 * b,1,1
 * 生产上很多问题 都是WC或者WC的变形
 **/
object Work01App {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName(this.getClass.getSimpleName)
    val sc = new SparkContext(sparkConf)
    sc.parallelize(List(
      List("a", 1, 3),
      List("a", 2, 4),
      List("b", 1, 1),
    )).map(x => {
      val key = x(0)
      val v1 = x(1).toString.toInt
      val v2 = x(2).toString.toInt
      (key,(v1,v2))
    }).reduceByKey((x,y) => {
      (x._1+y._1,x._2+y._2)
    }).map(x => (x._1,x._2._1,x._2._2)).foreach(println)

    sc.stop()
  }

}
