package com.cdcdata.spark

import com.cdcdata.spark.utils.ConfigUtils
import org.apache.kudu.spark.kudu.KuduContext
import org.apache.spark.sql.SparkSession

/**
  *主应用程序
  */
object RuozedataSparkApp {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().master("local[2]")
      .appName(this.getClass.getSimpleName)
      .getOrCreate()

    val kuduContext = new KuduContext(ConfigUtils.kuduMaster, spark.sparkContext)

    // TODO... 数据清洗
//    ETLProcessor.process(spark,kuduContext)
    // TODO... 数据分析(备注 生产上应该数据分析应该是并行的 而不是串行)
      //CountryCityProcessorT.process(spark,kuduContext)
    spark.stop()
  }

}
