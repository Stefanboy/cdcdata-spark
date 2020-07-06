package com.cdcdata.utils

import org.apache.spark.{SparkConf, SparkContext}

object ContextUtils {

  def getSparkContext(sparkConf:SparkConf)={
    new SparkContext(sparkConf)
  }

  def getSparkContext(appName:String,master:String="local[2]")={
    val sparkConf = new SparkConf().setAppName(appName).setMaster(master)
    new SparkContext(sparkConf)
  }
}
