package com.cdcdata.spark.work

import com.cdcdata.spark.utils.ETLUtils
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.lionsoul.ip2region.{DbConfig, DbSearcher}

/**
 * 第一阶段大考使用Spark SQL改造
 *   ETL改造 ==> Spark SQL采用Parquet格式
 * 服务器执行
 * spark-submit \
 * --class com.cdcdata.spark.work.StageETL \
 * --name StageETL \
 * --master yarn \
 * --jars /home/hadoop/lib/mysql-connector-java-5.1.27-bin.jar \
 * /home/hadoop/lib/cdcdata-spark-sql-1.0.jar \
 * /cdcdata/input/access.json /cdcdata/output
 *
 */
object StageETLV2 {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      //.master("local")
      //.appName(this.getClass.getSimpleName)
      .getOrCreate()
    val (input,output) = (args(0),args(1))
    etlJson(spark,input,output)

    spark.stop()
  }

  def etlJson(spark:SparkSession,input:String,output:String): Unit ={
    val df = spark.read.format("json").load(input)
    df.printSchema()
    df.show(10)
    val accessDF = df.select("adorderid", "adplatformproviderid", "requestmode", "ip", "iseffective", "isbilling", "processnode", "isbid", "iswin", "appid", "appname", "device", "sdkversion", "winprice", "adpayment")
    import spark.implicits._
    val accessDSStr = accessDF.map(x => {
      val config = new DbConfig
      val path = this.getClass.getResource("/ip2region.db").getPath
      val searcher = new DbSearcher(config, path)
      val ip = x.getString(3)
      val regionSplit: Array[String] = ETLUtils.parseIP(searcher, ip)
      val province: String = regionSplit(2) //省
      val city: String = regionSplit(3) //市
      AccessJson(x.getLong(0), x.getLong(1), x.getLong(2), x.getString(3), x.getLong(4), x.getLong(5), x.getLong(6), x.getLong(7), x.getLong(8), x.getString(9), x.getString(10), x.getString(11), x.getString(12), x.getDouble(13), x.getDouble(14), province, city).toString

    })
    accessDSStr.write.format("parquet").mode(SaveMode.Overwrite).save(output)

  }
  case class AccessJson(adorderid:Long,adplatformproviderid:Long,requestmode:Long,ip:String,iseffective:Long,isbilling:Long,processnode:Long,isbid:Long,iswin:Long,appid:String,appname:String,device:String,sdkversion:String,winprice:Double,adpayment:Double,province:String,city:String)
}
