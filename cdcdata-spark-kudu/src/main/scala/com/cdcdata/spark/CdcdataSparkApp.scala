package com.cdcdata.spark

import com.cdcdata.spark.analysis.ETLProcessor
import org.apache.spark.sql.SparkSession

object CdcdataSparkApp {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName(this.getClass.getName)
      .master("local[2]")
      .getOrCreate()

    // TODO... 数据清洗
    ETLProcessor.process(spark)




    spark.stop()

  }

}
