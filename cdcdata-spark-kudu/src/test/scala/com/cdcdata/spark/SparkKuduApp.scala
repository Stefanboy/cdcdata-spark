package com.cdcdata.spark

import org.apache.spark.sql.SparkSession

/**
 * Spark对接kudu
 */
object SparkKuduApp {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName(this.getClass.getSimpleName)
      .master("local[2]").getOrCreate()

    val df = spark.read.format("org.apache.kudu.spark.kudu")
      .option("kudu.master", "jd:7051")
      .option("kudu.table", "student")
      .load()

    df.show()

    spark.stop()
  }

}
