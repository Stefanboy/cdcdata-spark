package com.cdcdata.spark.rdd2df

import org.apache.spark.sql.{Dataset, SparkSession}

/**
 * author：若泽数据-PK哥   
 * 交流群：545916944
 */
object DSApp {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .master("local")
      .appName(this.getClass.getSimpleName)
      .getOrCreate()

    import spark.implicits._
    val df = spark.read.format("csv")
      .option("header","true")
      .option("inferSchema", "true")
      .load("ruozedata-spark-sql/data/sales.csv")

    val ds: Dataset[Sales] = df.as[Sales]

    //这两种获取某一列数据结果都是一样的 但是看一下执行计划却不一样
    val selectDF = df.select("customerId")
    val selectDS = ds.map(_.customerId)
    //打印出优化后的执行计划
    println(selectDF.queryExecution.optimizedPlan.numberedTreeString)
    println(".....................")
    println(selectDS.queryExecution.optimizedPlan.numberedTreeString)
    //区别ds底层会做一些优化
    //使用ds的好处 在编译期就报错 df和sql写错了 在运行时才报错 所以优先采用ds
    spark.stop()
  }

  case class Sales(transactionId:Int,
                   customerId:Int,
                   itemId:Int,
                   amountPaid:Double
                  )
}
