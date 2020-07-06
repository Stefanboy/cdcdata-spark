package com.cdcdata.spark.work

import java.util.Properties

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, Row, SaveMode, SparkSession}
import scalikejdbc.{DB, SQL}
import scalikejdbc.config.DBs

object PreWarning {
  def main(args: Array[String]): Unit = {

    System.setProperty("HADOOP_USER_NAME", "hadoop")
    val spark = SparkSession.builder
      .master("local[2]")
      .appName(this.getClass.getSimpleName)
      .enableHiveSupport()//开启HiveContext
      .getOrCreate()

    import spark.implicits._
    val df = spark.sql("select loginfo from logs where logtype='WARN' or logtype='ERROR'")
    df.show(false)
    //val ds = df.as[PrewarningConfig]
    val result = df.map(x => {
      x.getString(0).split(":")(1)
    }).flatMap(x => {
      x.split(" ")
    }).map((_, 1)).rdd.reduceByKey(_ + _)
      .sortBy(_._2, false).toDF("keywords","totalNums").limit(3)
    //result.foreach(println)
    val prop =new Properties()
    prop.setProperty("user","root")
    prop.setProperty("password","mysqladmin")
    result.select("keywords").write.mode(SaveMode.Append).jdbc("jdbc:mysql://jd:3306/bigdata","prewarning_config",prop)

/*    ds.map(x =>{
      x.keywords.split(":")(1)
    }).flatMap(x => {
      x.split(" ")
    }).map((_,1)).so*/

/*
    result.foreachPartition(partition =>{

      DBs.setupAll()//解析配置文件
      partition.foreach(pair => {
        DB.autoCommit({
          implicit session =>{
            //val sql = s"insert into wc(word,cnt) values ('${pair._1}',${pair._2}"
            SQL("insert into prewarning_config(keywords) values (?)")
              .bind(pair.getString(0))
              .update()
              .apply()
          }

        })
      })
      DBs.closeAll()
    })
*/


    spark.stop()
  }
  case class PrewarningConfig(keywords:String)
}
