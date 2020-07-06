package com.cdcdata.spark.source

import org.apache.spark.sql.SparkSession

object JsonSourceApp {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName(this.getClass.getSimpleName)
      .master("local")
      .getOrCreate()

    json1(spark)

  }

  /**
   * 输入数据
   * {"a":1,"b":2,"c":3}
   * {"a":4,:5,"c":6}
   * {"a":7,"b":8,"c":9}
   * 加载后的数据为 当出现_corrupt_record时 表示数据加载失败
   * +----------------+----+----+----+
   * | _corrupt_record|   a|   b|   c|
   * +----------------+----+----+----+
   * |            null|   1|   2|   3|
   * |{"a":4,:5,"c":6}|null|null|null|
   * |            null|   7|   8|   9|
   * +----------------+----+----+----+
   *
   * @param spark
   */
  def json1(spark: SparkSession): Unit = {
    val df = spark.read.json("cdcdata-spark-sql/data/test2.json")

    df.printSchema()
    df.show()

  }
}
