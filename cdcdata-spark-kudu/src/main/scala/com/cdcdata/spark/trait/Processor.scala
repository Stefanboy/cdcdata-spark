package com.cdcdata.spark.`trait`

import org.apache.spark.sql.SparkSession

/**
 * 定义接口
 * 使用Spark需实现该接口
 */
trait Processor {

  def process(spark:SparkSession)

}
