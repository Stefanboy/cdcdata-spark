package com.cdcdata.spark.work

import org.apache.spark.{SparkConf, SparkContext}

/**
 * 时间,网卡名，流量,机器名
 * 12:00:00,网卡1,5000,host1
 * 12:00:00,网卡2,1000,host1
 * 12:00:00,网卡1,100,host2
 *
 * 12:00:01,网卡1,6000,host1
 * 12:00:01,网卡2,2000,host1
 * 12:00:01,网卡1,200,host2
 *
 * 现在要在Spark中计算完成这样的计算
 * host1 ： (6000 + 2000) - (5000 + 1000) 除以 (12:00:01 - 12:00:00)
 * host2 : (200) - (100) 除以 (12:00:01 - 12:00:00)
 */
object FlowApp {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[2]").setAppName("master")
    val sc = new SparkContext(conf)
    val rdd = sc.parallelize(List(("12:00:00", "网卡1", 5000, "host1"), ("12:00:00", "网卡2", 1000, "host1"), ("12:00:00", "网卡1", 100, "host2"),("12:00:01", "网卡1", 6000, "host1"), ("12:00:01", "网卡2", 2000, "host1"), ("12:00:01", "网卡1", 200, "host2")))
    val reduceByKeyResult2 = rdd.map((x => ((x._1, x._4), x._3))).reduceByKey(_ + _).map(x => (x._1._2, (x._1._1, x._2)))
    //(host2,(12:00:00,100))
    //(host2,(12:00:01,200))
    //(host1,(12:00:00,6000))
    //(host1,(12:00:01,8000))
    reduceByKeyResult2.reduceByKey((x,y) =>{
      val timeStr1 = x._1
      val timeStr2 = y._1
      val timeArr1 = timeStr1.split(":")
      val timeArr2 = timeStr2.split(":")
      val time1 = (timeArr1(0).toInt * 3600 + timeArr1(1).toInt * 60 + timeArr1(2).toInt)
      val time2 = (timeArr2(0).toInt * 3600 + timeArr2(1).toInt * 60 + timeArr2(2).toInt)
      val flow1 = x._2.toInt
      val flow2 = y._2.toInt
      var result = ("",0)
      if (time2 > time1){
        result =("", (flow2-flow1)/(time2-time1))
      } else {
        result =("", (flow1-flow2)/(time1-time2))
      }
      result
    }).map(x => (x._1,x._2._2)).collect().foreach(println)


    sc.stop()
  }

}
