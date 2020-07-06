package com.cdcdata.spark

import org.apache.spark.sql.SparkSession

object Test {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .master("local")
      .appName(this.getClass.getName)
      .getOrCreate()

    val rdd = spark.sparkContext.textFile("cdcdata-spark-sql/data/access.log")
      rdd.map(_.split(","))


    spark.stop()
  }

}
