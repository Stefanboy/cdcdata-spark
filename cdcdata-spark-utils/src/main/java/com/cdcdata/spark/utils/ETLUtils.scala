package com.cdcdata.spark.utils

import java.text.SimpleDateFormat
import java.util
import java.util.{Calendar, Date, Map}

import org.lionsoul.ip2region.DbSearcher

object ETLUtils {

  /**
   * 处理解析出来的年 月 日的工具类
   * 输入 2 输出02
   * 输入12 输出12
   *
   * @param time
   * @return String
   */
  def processTime(time: Int): String = {
    if (time < 10) return "0" + time
    time + ""
  }


  /**
   * 解析ip为省 市 运营商
   * 输入 ip
   * 输出 |山东|济南|联通
   *
   * @param searcher
   * @param ip
   * @return
   */
  def parseIP(searcher: DbSearcher, ip: String): Array[String] = {
    val dataBlock = searcher.memorySearch(ip.trim)
    //解析出的ip格式（格式：国家|大区|省份|城市|运营商）
    val region = dataBlock.getRegion
    val regionSplit = region.split("\\|")
    regionSplit
  }


  /**
   * 将字符串中的数据映射和解析到Access对象中
   * 输入 [09/02/2020:22:22:22 +0800]	183.128.111.156	-	271	-	GET	https://www.bilibili.com/video/av52167219	404	26	737	HIT
   * 输出 Access字段对应的值为 183.128.111.156	-	271	-	GET	https://www.bilibili.com/video/av52167219	26	26	737	HIT	浙江省	杭州市	电信	https	www.bilibili.com	video/av52167219	2020	07	09
   *
   * @param log      日志文件一行数据
   * @param searcher 解析ip的对象
   * @param format   时间格式化对象
   * @return
   * @throws Exception
   */
  @throws[Exception]
  def logParser(log: String, searcher: DbSearcher, format: SimpleDateFormat): Access = {
    val splitStr: Array[String] = log.split("\t")
    val split = splitStr.map(_.trim)
    val time: String = split(0)
    val ip: String = split(1)
    val proxyIp: String = split(2)
    val responseTime: String = split(3)
    val referer: String = split(4)
    val method: String = split(5)
    val url: String = split(6)
    val httpCode: String = split(7)
    val requestSize: String = split(8)
    val responseSize: String = split(9)
    val cache: String = split(10)
    //创建Access对象
    //时间字段 [09/02/2020:22:22:22 +0800]
    //SimpleDateFormat format = new SimpleDateFormat("[dd/MM/yyyy:HH:mm:ss +0800]");
    val date: Date = format.parse(time)
    val calendar: Calendar = Calendar.getInstance
    calendar.setTime(date)
    val year: Int = calendar.get(Calendar.YEAR) //2020
    val month: Int = calendar.get(Calendar.MONTH) + 1 //2 该工具类处理出来的month<10时见面没有0 我们想要的是02
    val day: Int = calendar.get(Calendar.DATE) //9  该工具类处理出来的day<10时见面没有0 我们想要的是09
    //ip字段
    //val path: String = this.getClass.getClassLoader.getResource("/ip2region.db").getPath
    val regionSplit: Array[String] = parseIP(searcher, ip)
    val province: String = regionSplit(2) //省
    val city: String = regionSplit(3) //市
    val isp: String = regionSplit(4) //运营商
    //url https://www.bilibili.com/video/av52167219
    /*access.setHttp("-");
            access.setDomain("-");
            access.setPath("-");*/
    val urlSpl: Array[String] = url.split("://")
    val http: String = urlSpl(0)
    val domainAndPath: String = urlSpl(1)
    var domain: String = null
    var pathU: String = null
    if (domainAndPath != null && !("" == domainAndPath)) {
      domain = domainAndPath.substring(0, domainAndPath.indexOf("/"))
      pathU = domainAndPath.substring(domainAndPath.indexOf("/") + 1)
    }
    //val userid: String = cacheMap.get(domain.trim)
    //System.out.println("userid==>" + userid + "domain==>" + domain)
/*    if (userid != null && !("" == userid)) {
      access.setUserid(userid)
      System.out.println(userid)
    }*/
    //TODO...responseSize
    //该字段有可能出异常,因为造数据的时候故意造的错误数据
    //该字段是一个严格值，即如果不是数字，就丢掉就可以了
    Access(ip,proxyIp,responseTime.toLong,referer,method,url,httpCode,requestSize.toLong,responseSize,cache,processTime(year),processTime(month),processTime(day),province,city,isp,http,domain,pathU)
  }

}
case class Access(ip:String,proxyIp:String,responseTime:Long,referer:String,method:String,url:String,httpCode:String,requestSize:Long,responseSize:String,cache:String,year:String,month:String,day:String,province:String,city:String,isp:String,http:String,domain:String,path:String){
  override def toString: String = {

    ip + "\t" + proxyIp + "\t" + responseTime + "\t" + referer + "\t" + method + "\t" + url + "\t" + httpCode + "\t" + requestSize + "\t" + responseSize + "\t" + cache + "\t" + province + "\t" + city + "\t" + isp + "\t" + http + "\t" + domain + "\t" + path + "\t" + year + "\t" + month + "\t" + day

  }



  //自定义数据源处理解析各字段


}
