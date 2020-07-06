package com.cdcdata.spark.topn

import org.apache.spark.{SparkConf, SparkContext}

/**
 *
 * 需求：按照域名求每个url访问次数最多的前2个
 * 其实就是分组求topn
 * 遍历的时候使用map进行遍历
 * 这种的缺点：分布式执行，如果数据量太大，会撑爆list
 * 这是最low的方法 生产上绝对不要用tolist这种方式 接下来迭代
 */
object TopNApp01 {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName(this.getClass.getSimpleName)
    val sc = new SparkContext(sparkConf)
    val topN = 2;
    val lines = sc.textFile("file:///home/hadoop/data/site.log")
    val groupByDomain = lines.map(x => {
      val splits = x.split(",")
      val domain = splits(0)
      val url = splits(1)
      ((domain, url), 1)
    }).reduceByKey(_ + _) groupBy (_._1._1)

    groupByDomain.mapValues(x => {
      x.toList.sortBy(-_._2).map(x => (x._1._2,x._2)).take(topN)
    }).foreach(println)
    //如果x的数据过大 会将list撑爆 这种方式不可取  为什么take(topN)不执行job


    sc.stop()
  }
}
