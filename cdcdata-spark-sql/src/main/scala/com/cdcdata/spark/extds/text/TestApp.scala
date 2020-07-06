package com.cdcdata.spark.extds.text

import com.cdcdata.spark.rdd2df.DataFrameRDDApp.programmatic
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 * 测试自定义外部数据源
 */
object TestApp {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .master("local")
      .appName(this.getClass.getSimpleName)
      .getOrCreate()

    //加载方式1
    val df = spark.read.format("com.cdcdata.spark.extds.text")
      .load("cdcdata-spark-sql/data/extds/cdcdatainput.txt")
    df.printSchema()
    df.show()
    //加载方式2
    /*spark.read.format("com.cdcdata.spark.extds.text")
      .option("path","cdcdata-spark-sql/data/extds").load().show()*/


    spark.stop()
  }



}
