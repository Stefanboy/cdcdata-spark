package com.cdcdata.spark.domain

/**
 * ip解析对应的实体类
 * @param ip ip
 * @param latitude 维度
 * @param longitude 经度
 * @param country 国家
 * @param city 城市
 */
case class LatitudeLongitude(ip: String,
                             latitude: String,
                             longitude: String,
                             country: String,
                             city: String
          )