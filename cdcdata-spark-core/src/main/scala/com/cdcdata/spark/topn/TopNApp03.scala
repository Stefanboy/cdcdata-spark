package com.cdcdata.spark.topn

import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable

/**
 * 引入partitionBy 自定义分区器
 * 把相同的domain丢到一个分区去
 */
object TopNApp03 {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName(this.getClass.getSimpleName)
    val sc = new SparkContext(sparkConf)

    val topN = 2;
    val lines = sc.textFile("cdcdata-spark-core/data/site.log")
    val processRDD = lines.map(x => {
      val splits = x.split(",")
      val domain = splits(0)
      val url = splits(1)
      ((domain, url), 1)
    })
    val domains = processRDD.map(_._1._1).distinct().collect()
/*    val result = processRDD.reduceByKey(_ + _)
    //自定义分区器的getPartition的参数跟result有关 就是result的key
    result.partitionBy(new CdcdataPartitioner(domains)).mapPartitions(partition => {
      //又绕回到转成list的方式去了。。。 不慌  但是这种方式在ui的job数量明显减少了
      partition.toList.sortBy(-_._2).take(topN).iterator
    }).collect()*/

    //迭代1 合并partitionBy和reduceByKey  reduceByKey有一个指定分区器的方法
/*    val result = processRDD.reduceByKey(new CdcdataPartitioner(domains), _ + _)
    result.mapPartitions(partition => {
      partition.toList.sortBy(-_._2).take(topN).iterator
    }).foreach(println)*/

    //迭代最终版
    //更精简的隐式转换
    /**
     * (Double,Int) 定义排序规则的返回值类型 即按照这俩参数进行排序
     * (String,Double,Int) 进来数据的类型
     * x=>(-x._2,x._3) 定义排序规则
     * 先降序 如果相同在升序排
     */
    val result = processRDD.reduceByKey(new CdcdataPartitioner(domains), _ + _)
    implicit val ord = Ordering[(Int,String)].on[((String,String),Int)](x=>(-x._2,x._1._2))
    val ts = new mutable.TreeSet[((String,String), Int)]()
    result.mapPartitions(partition => {
      // TODO... Tree...  order

      partition.foreach(x => {
        /**
         * TODO... treeSet.add(x)
         *
         * if(treeSet.size > topN) {
         *    treeSet.remove(treeSet.last)
         * }
         */

        ts.add(x)
        if(ts.size > 3){
          ts.remove(ts.last)
        }
        //ts
      })
      //println(ts)
      ts.iterator

    }).foreach(println)

    sc.stop()
  }

/*class MyOrdering extends Ordering[String] {
  override def compare(x: String, y: String): Int = {
    // x或者y的格式为："zs 90"
    val xFields = x.split(" ")
    val yFields = y.split(" ")
    val xScore = xFields(1).toInt
    val yScore = yFields(1).toInt
    val ret = yScore - xScore
    ret
  }*/
}
