package com.cdcdata.spark.strengthen

import org.apache.spark.{SparkConf, SparkContext}

/**
 * 需求
 * 用户     节目               列表  点击
 * 001,一起看|电视剧|军旅|亮剑,1,1
 * 001,一起看|电视剧|军旅|亮剑,1,0
 * 002,一起看|电视剧|军旅|士兵突击,1,1
 * ==>
 * 001,一起看,2,1
 * 001,电视剧,2,1
 * 001,军旅,2,1
 * 001,亮剑,2,1
 *
 * 要看一下groupbukey和reducebykey的DAG图 看一下这两种方式shuffle的数据量大小
 **/
object Work02App {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName(this.getClass.getSimpleName)
    val sc = new SparkContext(sparkConf)
    val lines = sc.parallelize(List(
      "001,一起看|电视剧|军旅|亮剑,1,1",
      "001,一起看|电视剧|军旅|亮剑,1,0",
      "002,一起看|电视剧|军旅|士兵突击,1,1"
    ))
    lines.flatMap(x => {
      val splits = x.split(",")
      val id = splits(0)
      val word = splits(1)
      val imp = splits(2).toInt
      val click = splits(3).toInt
      val words = word.split("\\|")
      words.map(x => {
        ((id, x), (imp, click))
      })
    }).groupByKey().mapValues(x => {//groupByKey方式
      val imps = x.map(_._1).sum
      val clicks = x.map(_._2).sum
      (imps,clicks)
    }).foreach(println)

    //reduceByKey方式计算
    //.reduceByKey((a,b) =>(a._1+b._1,a._2+b._2)).foreach(println)

    sc.stop()
  }

}
