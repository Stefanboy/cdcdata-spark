package com.cdcdata.spark.source

import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.{SaveMode, SparkSession}

object CsvSourceApp {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName(this.getClass.getSimpleName)
      .master("local")
      .getOrCreate()

    csv(spark)

  }

  /**
   * 输入csv的类型
   * a|b|c
   * 1|2|3
   * 4|aaa|6
   * 7|8|9.8
   * 如果不设置类型推导 运行加载后的数据类型都是string
   * 设置类型推导后 运行加载后的数据类型会进行兼容
   * |-- a: integer (nullable = true)
   * |-- b: string (nullable = true)
   * |-- c: double (nullable = true)
   *
   * @param spark
   */
  def csv(spark: SparkSession): Unit = {
    //header 表示有头 seq 表示分隔符为; inferSchema表示推导schema的类型
    val df = spark.read.format("csv")
      .option("header", "true")
      .option("sep", "|")
      .option("inferSchema","true")
      .load("cdcdata-spark-sql/data/test.csv")

    df.printSchema()
    df.show()

  }
}
