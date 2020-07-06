package com.cdcdata.spark.topn

import org.apache.spark.{SparkConf, SparkContext}

/**
 *这种方式会触发多个job sortby很take都会触发  循环调用 ui会有许多个job显示
 * 域名写死了也不行
 * 但是最起码比第一个版本要好 接下来继续迭代
 * RDD不能嵌套执行 如果想要嵌套的话RDD得先执行action
 */
object TopNApp02 {
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
    //返回一个array  不会发生oom这种情况  因为域名本来也不会太多
    val domains: Array[String] = processRDD.map(_._1._1).distinct().collect()
    //RDD不能嵌套
    domains.map(x => {
      processRDD.filter(_._1._1 == x).reduceByKey(_+_).sortBy(-_._2).take(topN)
    })




    sc.stop()

  }
}
