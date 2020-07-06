package com.cdcdata.spark.work.extds.text

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import com.cdcdata.spark.extds.text.Utils
import com.cdcdata.spark.utils.ETLUtils.{parseIP, processTime}
import org.apache.spark.internal.Logging
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.sources.{BaseRelation, TableScan}
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, SQLContext}
import org.lionsoul.ip2region.{DbConfig, DbSearcher}

import scala.collection.mutable.ArrayBuffer

/**
 * 自定义外部数据源 读取txt
 * @param sqlContext
 * @param path
 */
class ETLTextDataSourceRelation(val sqlContext:SQLContext, val path:String)
  extends BaseRelation with TableScan with Logging with Serializable {
  //定义scheme
  override def schema: StructType = StructType(
      StructField("ip",StringType,false)::
      StructField("proxyIp",StringType,false)::
      StructField("responseTime",LongType,false)::
      StructField("referer",StringType,false)::
      StructField("method",StringType,false)::
      StructField("url",StringType,false)::
      StructField("httpCode",StringType,false)::
      StructField("requestSize",LongType,false)::
      StructField("responseSize",LongType,false)::
      StructField("cache",StringType,false)::
      StructField("year",StringType,false)::
      StructField("month",StringType,false)::
      StructField("day",StringType,false)::
      StructField("province",StringType,false)::
      StructField("city",StringType,false)::
      StructField("isp",StringType,false)::
      StructField("http",StringType,false)::
      StructField("domain",StringType,false)::
      StructField("path",StringType,false):: Nil
  )
  //定义buildScan 组装RDD[Row]
  override def buildScan(): RDD[Row] = {

    logError("ETL清洗使用自定义数据源实现:buildScan")
    //通过spark拿到输入的内容 这是一个RDD[Row]
    val info = sqlContext.sparkContext.textFile(path)
    //拿到StructField
    val schemaFields: Array[StructField] = schema.fields

    val rddStr = info.mapPartitions(partiton => {
      val config = new DbConfig
      val path = this.getClass.getResource("/ip2region.db").getPath
      val searcher = new DbSearcher(config, path)
      val format = new SimpleDateFormat("[dd/MM/yyyy:HH:mm:ss +0800]")
      val arrBuff = ArrayBuffer[String]()
      partiton.foreach(log => {
        val str = etlLogParser(log, searcher, format)
        arrBuff += str
      })
      arrBuff.toArray.iterator

    })
    val rddFilter: RDD[String] = rddStr.filter(_.split("\t")(8) != "-")
    val rddArr: RDD[Array[String]] = rddFilter.map(_.split("\t"))
    val rddResult: RDD[Array[Any]] = rddArr.map(x => x.zipWithIndex.map {
      case (value, index) => {
        //val columnName = schemaFields(index).name
        Utils.castTo(value, schemaFields(index).dataType)
      }
    })
    val row = rddResult.map(x => Row.fromSeq(x))
    row
  }


  def etlLogParser(log: String, searcher: DbSearcher, format: SimpleDateFormat): String = {
    val splitStr: Array[String] = log.split("\t")
    val split = splitStr.map(_.trim)
    val time = split(0)
    val ip = split(1)
    val proxyIp = split(2)
    val responseTime = split(3)
    val referer = split(4)
    val method = split(5)
    val url = split(6)
    val httpCode = split(7)
    val requestSize = split(8)
    val responseSize = split(9)
    val cache = split(10)
    //创建Access对象
    //时间字段 [09/02/2020:22:22:22 +0800]
    //SimpleDateFormat format = new SimpleDateFormat("[dd/MM/yyyy:HH:mm:ss +0800]");
    val date: Date = format.parse(time)
    val calendar: Calendar = Calendar.getInstance
    calendar.setTime(date)
    val year = calendar.get(Calendar.YEAR) //2020
    val month = calendar.get(Calendar.MONTH) + 1 //2 该工具类处理出来的month<10时见面没有0 我们想要的是02
    val day = calendar.get(Calendar.DATE) //9  该工具类处理出来的day<10时见面没有0 我们想要的是09
    //ip字段
    //val path: String = this.getClass.getClassLoader.getResource("/ip2region.db").getPath
    val regionSplit: Array[String] = parseIP(searcher, ip)
    val province = regionSplit(2) //省
    val city = regionSplit(3) //市
    val isp = regionSplit(4) //运营商
    //url https://www.bilibili.com/video/av52167219
    val urlSpl: Array[String] = url.split("://")
    val http = urlSpl(0)
    val domainAndPath = urlSpl(1)
    var domain:String = null
    var pathU:String = null
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

    //Access(ip,proxyIp,responseTime.toLong,referer,method,url,httpCode,requestSize.toLong,responseSize,cache,processTime(year),processTime(month),processTime(day),province,city,isp,http,domain,pathU)
    val acc = ip + "\t" + proxyIp + "\t" + responseTime + "\t" + referer + "\t" + method + "\t" + url + "\t" + httpCode + "\t" + requestSize + "\t" + responseSize + "\t" + cache + "\t" + year + "\t" + month + "\t" + day + "\t" + province + "\t" + city + "\t" + isp + "\t" + http + "\t" + domain + "\t" + path
    acc
  }
}

