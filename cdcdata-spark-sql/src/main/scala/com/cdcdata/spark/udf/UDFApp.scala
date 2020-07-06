package com.cdcdata.spark.udf

import org.apache.spark.sql.{SaveMode, SparkSession}

object UDFApp {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName(this.getClass.getSimpleName)
      .master("local")
      .enableHiveSupport()//开启HiveContext
      .getOrCreate()

    import spark.implicits._
    val df = spark.sparkContext.textFile("cdcdata-spark-sql/data/likes.txt")
      .map(x => {
        val split = x.split("\t")
        Likes(split(0), split(1))
      }).toDF()
    //注册成一张表
    df.createOrReplaceTempView("teams")
    //注册UDF函数
    val teamslengthUDF = spark.udf.register("teams_length", (input: String) => {
      input.split(",").length
    })
    //执行查询
    //spark.sql("select name,teams,teams_length(teams) as teams_length from teams").show()

    //API方式
    //df.select($"name",$"teams",teamslengthUDF($"teams")).show()

    //写出去
    //df.write.format("orc").mode(SaveMode.Overwrite).save("cdcdata-spark-sql/out")
    df.write.format("parquet").mode(SaveMode.Overwrite).save("cdcdata-spark-sql/out")



  }
  case class Likes(name: String, teams: String)
}
