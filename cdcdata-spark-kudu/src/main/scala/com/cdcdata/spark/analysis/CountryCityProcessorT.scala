package com.cdcdata.spark.analysis

import com.cdcdata.spark.`trait`.ProcessorT
import com.cdcdata.spark.utils.{ConfigUtils, ContantsSQL}
import org.apache.kudu.spark.kudu.KuduContext
import org.apache.spark.sql.SparkSession

/**
  * author：若泽数据-PK哥
  * 交流群：545916944
  */
object CountryCityProcessorT extends ProcessorT {
  override def process(spark: SparkSession,kuduContext: KuduContext): Unit = {
    //读进来数据是parqet格式
    //val ods = spark.read.load("out/ods")

    val ods = spark.read.format("org.apache.kudu.spark.kudu")
      .option("kudu.master", ConfigUtils.kuduMaster)
      .option("kudu.table", ConfigUtils.ods)
        .load()
    ods.createOrReplaceTempView("ods")

    val result = spark.sql(ContantsSQL.COUNTRY_CITY_SQL)
    result.show(false)
  }
}
