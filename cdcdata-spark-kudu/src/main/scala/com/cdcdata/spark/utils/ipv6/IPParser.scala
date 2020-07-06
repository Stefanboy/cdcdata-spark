package com.cdcdata.spark.utils.ipv6

import java.util

import com.cdcdata.spark.domain.LatitudeLongitude
import com.cdcdata.spark.utils.ConfigUtils
import com.maxmind.geoip.LookupService

import scala.collection.JavaConverters

/**
 * 解析ip并放入seq中返回
 */
object IPParser {

  //得到ip库的路径
  private val geoLiteCity: String = ConfigUtils.geoLiteCity

  def parserIP(ips:List[String]):Seq[LatitudeLongitude] = {
    /**
     * 拿到服务
     * LookupService 参数1 文件的路径 参数2 模式
     */
    val lookupService = new LookupService(geoLiteCity,LookupService.GEOIP_MEMORY_CACHE)
    val array = new util.ArrayList[LatitudeLongitude]()
    for(ip <- ips) {

      // TODO... 商业库IP解析

      val location = lookupService.getLocation(ip)
      //维度
      val latitude = location.latitude
      //经度
      val longitude = location.longitude
      val country = location.countryName
      val city = location.city
      array.add(new LatitudeLongitude(ip,latitude+"",longitude+"",country,city))
    }
    //转成seq格式数据返回
    JavaConverters.asScalaIteratorConverter(array.iterator()).asScala.toSeq
  }
}
