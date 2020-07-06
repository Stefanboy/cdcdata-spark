package com.cdcdata.spark.analysis

import com.cdcdata.spark.`trait`.Processor
import org.apache.spark.sql.SparkSession

/**
 * aggregate详解
 * aggregateByKey详解
 * combineByKey详解
 * combineByKeyWithClassTag详解
 */
object AggregationApp extends Processor{
  override def process(spark: SparkSession): Unit = {

    aggregateAnalysis(spark)
    //aggregateByKeyAnalysis(spark)
    //combineByKeyAnalysis(spark)
    //combineByKeyWithClassTagAnalysis(spark)

  }

  def aggregateAnalysis(spark: SparkSession): Unit ={
    /**
     * aggregate详解
     */

    val sc = spark.sparkContext
    val rdd1 = sc.parallelize(1 to 5, 1)
    def func1(a:Int, b:Int) = a * b
    def func2(a:Int, b:Int) = a + b
    //结果是363 如果将分区设为3 计算结果为84 如果将分区设为2 计算结果为189
    rdd1.aggregate(3)(func1, func2)

    //查看分区操作
    //rdd1.mapPartitionsWithIndex()

    //求rdd的和 不允许使用sum 那就使用agg就可以
    val rdd = sc.parallelize(List(1, 3, 2, 4, 3, 5), 3)
    rdd.aggregate(0)((a:Int,b:Int)=>a+b,(a:Int,b:Int)=>a+b)
    //简写为  结果为18
    rdd.aggregate(0)(_+_,_+_)

    // 需求 求每个分区的最大值之和： 即结果为 3 + 4 + 5 = 12
    val rdd2 = sc.parallelize(List(List(1,3),List(2,4),List(3,5)), 3)
    def fun01(x:Int,y:List[Int]) = {
      x.max(y.max)
    }
    def fun02(a:Int, b:Int) = a + b
    //结果 12
    rdd2.aggregate(0)(fun01,fun02)
    //这个的结果是40
    rdd2.aggregate(10)(fun01,fun02)
  }

  def aggregateByKeyAnalysis(spark: SparkSession): Unit ={
    /**
     * aggregateByKey详解
     * 问题：看看那个值在哪个分区
     */
    val sc = spark.sparkContext
    val rdd = sc.parallelize(List(("a",3),("a",2),("c",4),("b",3),("c",6),("c",8)), 2)
    //求分区内key对应的最大值，然后求和
    //结果是Array(("a",3),("b",3),("c",12))
    rdd.aggregateByKey(10)(math.max(_,_),_+_).collect()
    //求每个key出现的次数 (1,3)表示1出现3次
    val rdd2 = sc.parallelize(List((1,3),(1,2),(1,4),(2,3),(3,8),(3,6)), 2)
    //结果是Array((3,14),(1,9),(2,3))
    rdd2.foldByKey(0)(_+_).collect()
  }

  def combineByKeyAnalysis(spark: SparkSession): Unit ={
    /**
     * combineByKey详解
     */
    val sc = spark.sparkContext
    val rdd2 = sc.parallelize(List((1,3),(1,4),(1,2),(2,3),(3,6),(3,8)), 3)
    //reduceByKey都会用  结果是Array((3,14),(1,9),(2,3))
    rdd2.reduceByKey(_+_).collect()
    //使用combineByKey如何实现这样的需求呢 即key相同时 value累加

    //结果是Array((3,14),(1,9),(2,3))
    rdd2.combineByKey(
      x => x,
      (a:Int,b:Int) =>a+b,
      (x:Int,y:Int) => x+y
    ).collect()

    val rdd3 = sc.parallelize(List(("a",88),("b",95),("a",91),("b",93),("a",95),("b",98)), 2)
    //求平均数
    // (_,1)的作用是计数功能 比如进来的值88 出去的值(88,1) 95=>(95,1)
    //结果Array((b,95.33xxxx),(a,91.333xxx))
    rdd3.combineByKey(
      (_,1),
      (a:(Int,Int),b) => (a._1+b,a._2+1),
      (x:(Int,Int),y:(Int,Int)) => (x._1+y._1,x._2+y._2)
    ).map{
      case (k,v) => (k,v._1/v._2.toDouble)
    }.collect()
  }


  def combineByKeyWithClassTagAnalysis(spark: SparkSession): Unit ={
    val sc = spark.sparkContext
    val rdd2 = sc.parallelize(List((1,3),(1,4),(1,2),(2,3),(3,6),(3,8)), 3)

    //终于到了最后一个算子combineByKeyWithClassTag
    //xxxbyKey算子归根到底都是用的combineByKeyWithClassTag
    //结果是Array((3,14),(1,9),(2,3))
    rdd2.combineByKeyWithClassTag(
      x => x,
      (a:Int,b:Int) =>a+b,
      (x:Int,y:Int) => x+y
    ).collect()
  }

}
