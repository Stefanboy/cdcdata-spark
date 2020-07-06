package com.cdcdata.spark.work

import org.apache.spark.{SparkConf, SparkContext}

/**
 * 特殊的join 窄依赖
 */
object NDJoin {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[2]").setAppName("master")
    val sc = new SparkContext(conf)
    sc.setLogLevel("ERROR")
    //如果JoinAPI之前被调用的RDD API是宽依赖(存在shuffle), 而且两个join的RDD的分区数量一致，join结果的rdd分区数量也一样，这个时候join api是窄依赖
    //　　除此之外的，rdd 的join api是宽依赖
    //在执行join算子之前如果先执行groupByKey，执行groupByKey或reduceByKey之后，会把相同的key分到同一个分区，再执行join算子，
    // join算子是把key相同的进行join（只是对于k v形式的数据可以使用），不一定会产生shuffle ，有可能发生shuffle，也有可能不发生

    //这种的join是窄依赖，因为分区里的数据key都是一样的，shuffle时，父RDD里的分区数据只能发到一个子RDD
    val rdd1 = sc.parallelize(List(("aaa",1),("bbb",1),("ccc",1)),3).reduceByKey(_+_)
    val rdd2 = sc.parallelize(List(("aaa",2),("bbb",2),("ccc",2)),3).reduceByKey(_+_)
    rdd1.join(rdd2).collect()


    /*val rdd1 = sc.parallelize(Array((1, "张三1"), (1, "张三2"), (2, "李四"), (3, "王五"), (4, "Tom"), (5, "Gerry"), (6, "莉莉")), 1).map(x => (x, null)).reduceByKey((x,y) => x, 1).mapPartitions(
           iter => iter.map(tuple => tuple._1), true)

    val rdd2 = sc.parallelize(Array((1, "上海"), (2, "北京1"), (2, "北京2"), (3, "南京"), (4, "纽约"), (6, "深圳"), (7, "香港")), 1).map(x => (x, null)).reduceByKey((x,y) => x, 1).mapPartitions(
      iter => iter.map(tuple => tuple._1), true)


    val joinResultRDD = rdd1.join(rdd2).map {
      case (id, (name, address)) => {
        (id, name, address)}}*/


    sc.stop()
  }

}
