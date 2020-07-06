package com.cdcdata.spark.source

import org.apache.spark.sql.SparkSession

object SparkSessionApp {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder
    .master("local")
    .appName("Word Count")
    .config("spark.some.config.option", "some-value")
    .getOrCreate()
    //在命令行读取
    val df = spark.read.json("file:///sparkhome/examples/src/main/resources/people.json")
    df.printSchema()//打印出scheme的信息
    df.show()//查看数据信息
    df.createOrReplaceTempView("people") //注册成临时表
    //会报错 因为没有mysql驱动 重新启动 spark-shell --jars ~/lib/mysql....
    spark.sql("select * from people where age > 19")
    df.persist()
    //传入普通的sql字符串生成逻辑执行计划，然后生成优化后的逻辑执行计划，然后生成物理执行计划，生后的物理执行计划
    //点进sql方法 642行 点击sqlParser
    // 66行点击ParserInterface 这是一个解析的接口 看一下实现类
/*    val df = spark.sql("xxx")
    df.createTempView("")*/


  }

}
