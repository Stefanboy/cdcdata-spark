package com.cdcdata.spark.utils

import com.typesafe.config.ConfigFactory

/**
  * 读取配置文件中的参数值
  */
object ConfigUtils {
  //加载配置文件
  def conf = ConfigFactory.load()

  def geoLiteCity = conf.getString("geo.lite.city")

  def odsOutput = conf.getString("ods.output")

  def stroageFormat = conf.getString("storageformat")

  def kuduMaster = conf.getString("kudu.master")

  def ods = conf.getString("kudu.ods")
  def countryCity = conf.getString("kudu.country_city")
}
