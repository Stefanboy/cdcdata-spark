package com.cdcdata.spark.work.extds.text

import org.apache.spark.sql.SparkSession

object TestApp {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .master("local")
      .appName(this.getClass.getName)
      .getOrCreate()

    val df = spark.read.format("com.cdcdata.spark.work.extds.text")
      .load("cdcdata-spark-sql/data/access1.log")
    df.printSchema()
    df.show()


    spark.stop()
  }

}
