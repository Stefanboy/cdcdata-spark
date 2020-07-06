package com.cdcdata.spark.catalog

import org.apache.spark.sql.SparkSession

/**
 * author：若泽数据-PK哥   
 * 交流群：545916944
 *
 */
object CatalogApp {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .master("local")
      .appName(this.getClass.getSimpleName)
      .getOrCreate()
import spark.implicits._
    val catalog = spark.catalog
    ////拿出所有的数据库
    catalog.listDatabases().show(false)
    //拿出所有的数据库的名字
    catalog.listDatabases().map(_.name).show
    //拿出数据库所有表 过滤出表名包含emp的
    catalog.listTables("ruozedata_hive").filter('name.contains("emp")).show

    catalog.listTables("ruozedata_hive").printSchema()
    //判断该表是否缓存
    catalog.isCached("ruozedata_hive.emp")
    //对该表进行缓存 这种缓存是lazy的
    catalog.cacheTable("ruozedata_hive.emp")
    //去除缓存
    catalog.uncacheTable("ruozedata_hive.emp")
    //查看有那些函数 并过滤出name为str_length的函数
    catalog.listFunctions().filter('name === "str_length").show()
    //注册函数
    spark.udf.register("str_length",(str:String)=>str.length)
    spark.stop()
  }
}
