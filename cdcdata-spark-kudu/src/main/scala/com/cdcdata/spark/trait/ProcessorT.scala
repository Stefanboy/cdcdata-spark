package com.cdcdata.spark.`trait`

import org.apache.kudu.spark.kudu.KuduContext
import org.apache.spark.sql.SparkSession

/**
 * 定义接口
 * 使用Spark需实现该接口
 */
trait ProcessorT {

  def process(spark:SparkSession,kuduContext: KuduContext)

}
