package com.cdcdata.spark.topN

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.Window

object LogApp {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .master("local")
      .appName(this.getClass.getSimpleName)
      .getOrCreate()

    import spark.implicits._
    val df = spark.read.textFile("cdcdata-spark-sql/data/access.log")
      .map(x => {
        val split = x.split("\t")
        val platform = split(15)
        val province = split(11)
        val city = split(12)
        val traffic = split(3).toLong
        val isp = split(1)

        // TODO... 清洗操作
        (platform, province, city, traffic, isp)
      }).toDF("platform", "province", "city", "traffic", "isp")

    //原生sql查询
    /*df.createOrReplaceTempView("log")
    spark.sql("select platform,province,city,sum(traffic) as traffics from log group by platform,province,city order by traffics desc")
      .show()*/
    //调用api查询
    /*    import org.apache.spark.sql.functions._
        df.groupBy("platform","province","city")
          .agg(sum("traffic").as("traffics"))
          .sort('traffics.desc)
          .show()*/

    // TODO... 按照platform分组，province访问次数最多的TopN
/*    val topNSQL =
      """select * from
        |(
        |select t.*,row_number() over(partition by platform order by cnt desc) as r
        |from
        |(select platform,province,city,count(1) cnt from log group by platform,province,city) t
        |) a where a.r<=3""".stripMargin

    spark.sql(topNSQL).show()*/


    //API方式
    import org.apache.spark.sql.functions._
    //Window.partitionBy()
    val groupByDF = df.groupBy($"platform", $"province", $"city")
      .agg(count("platform").as("cnt"))
      .select($"platform", $"province", $"city", $"cnt")
    //groupByDF.createOrReplaceTempView("groupByDFTable")
    val winOver = Window.partitionBy($"platform").orderBy($"cnt".desc)
    val topResult = groupByDF.withColumn("top", row_number().over(winOver)).where($"top" <= 3).drop("top")
    topResult.show()

  }

}
