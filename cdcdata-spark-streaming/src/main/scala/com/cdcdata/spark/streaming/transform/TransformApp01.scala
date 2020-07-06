package com.cdcdata.spark.streaming.transform

import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ListBuffer

/**
 * 使用RDD做一个过滤黑名单的功能
 *
 */
object TransformApp01 {

  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName(this.getClass.getName)
    val sc = new SparkContext(sparkConf)

    //过滤、黑名单 ==> 生产上一些数据的特殊处理

    //黑名单信息
    val blacks = new ListBuffer[(String,Boolean)]()
    blacks.append(("xingxing",true))
    val blacksRDD = sc.parallelize(blacks)

    /**
     * 输入数据的信息
     * 名字，完整信息
     * (pk,(pk,30))
     * (xingxing,(xingxing,60))
     * (ruoze,(ruoze,18))
     */
    val inputs = new ListBuffer[(String,String)]()
    inputs.append(("pk","pk,30"))
    inputs.append(("xingxing","xingxing,60"))
    inputs.append(("ruoze","ruoze,18"))
    val inputsRDD = sc.parallelize(inputs)

    //结果:只输出不在黑名单的数据
    //这种join的方式不是最好的 因为有shuffle的存在 最优的是将小的数据广播出去 然后再做操作
    val joinRDD = inputsRDD.leftOuterJoin(blacksRDD)
    joinRDD.filter(x => {
      x._2._2.getOrElse(false) != true
    }).map(_._2._1).collect().foreach(println)





    sc.stop()
  }

}
