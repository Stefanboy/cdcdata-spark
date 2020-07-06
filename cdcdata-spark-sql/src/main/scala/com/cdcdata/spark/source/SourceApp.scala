package com.cdcdata.spark.source

import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.{SaveMode, SparkSession}

object SourceApp {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName(this.getClass.getSimpleName)
      .master("local")
      .getOrCreate()

    json(spark)
    //text(spark)
    //csv(spark)
    //jdbc(spark)
    //jdbc2(spark)
    spark.stop()
  }

  /**
   * 读取数据提高并行度
   * 写数据 指定追加和指定字段类型
   * @param spark
   */
  def jdbc2(spark: SparkSession): Unit = {
    //jdbc1是采用硬编码的方式 不可取
    val config = ConfigFactory.load()
    val url = config.getString("db.default.url")
    val user = config.getString("db.default.user")
    val password = config.getString("db.default.password")
    val srctable = config.getString("db.default.srctable")
    val targettable = config.getString("db.default.targettable")
    val driver = config.getString("db.default.driver")
    val database = config.getString("db.default.database")
    val partitionColumn = config.getString("db.default.partitionColumn")
    val lowerBound = config.getString("db.default.lowerBound")
    val upperBound = config.getString("db.default.upperBound")
    val numPartitions = config.getString("db.default.numPartitions")
    val df = spark.read.format("jdbc")
      .option("url", url)
      .option("dbtable", database + "." + srctable)
      .option("user", user)
      .option("password", password)
      .option("driver", driver)
      .option("partitionColumn",partitionColumn)
      .option("lowerBound",lowerBound)
      .option("upperBound",upperBound)
      .option("numPartitions",numPartitions)
      .load()

    df.printSchema()
    df.where("deptno = 20").write
      .format("jdbc")
      .option("url", url)
      .option("dbtable", database+"."+targettable)
      .option("user", user)
      .option("password", password)
      .option("driver", driver)
      .option("createTableColumnTypes", "ename varchar(50),job varchar(50),hiredate datetime")
      .mode(SaveMode.Append)
      .save()


  }


  def jdbc(spark: SparkSession): Unit = {
    //读mysql的数据
    val df = spark.read.format("jdbc")
      .option("url", "jdbc:mysql://jd:3306")
      .option("dbtable", "bigdata.emp")
      .option("user", "root")
      .option("password", "mysqladmin")
      .load()
    //该行代码是为了看join方法的注释以及jobtype支持的类型
    //df.join(df)

    df.printSchema()
    df.show()

    import spark.implicits._
    //写数据到mysql
    df.where('deptno === 20)
      .write
      .format("jdbc")
      .option("url", "jdbc:mysql://jd:3306")
      .option("dbtable", "bigdata.emp_2")
      .option("user", "root")
      .option("password", "mysqladmin")
      .option("createTableColumnTypes", "ename varchar(50),job varchar(50)")
      .mode(SaveMode.Append)
      .save()
  }

  def csv(spark: SparkSession): Unit = {
    //header 表示有头 seq 表示分隔符为; inferSchema表示推导schema的类型
    val df = spark.read.format("csv")
      .option("header", "true")
      .option("sep", ";")
      .option("inferSchema","true")
      .load("cdcdata-spark-sql/data/people.csv")

    df.printSchema()
    df.show()

  }

  def text(spark:SparkSession): Unit ={
    import spark.implicits._
    val df = spark.read.format("text").load("cdcdata-spark-sql/data/people.txt")
    //df.printSchema()
   // df.show()

    //ds方式 能直接调用split
    val ds = spark.read.textFile("cdcdata-spark-sql/data/people.txt")
    ds.printSchema()
/*    ds.map(x => {
      val split = x.split(",")
      (split(0),split(1))
    }).show()*/

    val ds2 = ds.map(x => {
      val split = x.split(",")
      (split(0)+","+ split(1))
    })

    //写出数据 不支持写入这样的两行  要将两行合并为一行，并且只支持输出string 其他字段都转为int输出
    //ds2.write.format("text").mode(SaveMode.Overwrite).save("cdcdata-spark-sql/out")

    //输出压缩格式的数据 进入CompressionCodecs.scala 里面有支持的压缩格式
    ds2.write.format("text")
      .option("compression","gzip")
      .mode(SaveMode.Overwrite)
      .save("cdcdata-spark-sql/out")

    //df方式遍历出字符串的字段.getString(0)
/*    df.map(x => {
      val split = x.getString(0).split(",")
      (split(0),split(1))
    }).show()*/

    //df转成rdd方式
/*    df.rdd.map(x => {
      val split = x.getString(0).split(",")
      (split(0),split(1))
    }).foreach(println)*/
  }

  def json(spark:SparkSession): Unit ={
    //标准的读取数据源的写法
    val df = spark.read.format("json").load("cdcdata-spark-sql/data/access.json")
    //也是读取json文件的一种方式 其实还是调用的format("json").load
    //val df2 = spark.read.json("cdcdata-spark-sql/data/access.json")
    df.printSchema()//打印schema信息
    //df.show()//展示数据
    //查询出指定字段并展示出来 show(true) 表示如果字段过长，截断
    //df.select("sessionid","sdkversion","advertisersid").show(false)

    import spark.implicits._
    //查询出指定字段并展示出来，方式2 这种方式推荐，因为如果字段中有属性 可以直接.xxx 将属性值也展示出来
    //df.select($"sessionid",$"sdkversion",$"advertisersid").show(true)
    val df3 = df.select($"sessionid", $"sdkversion", $"advertisersid")
    //条件查询跟Filter(条件)是一个用法 条件里面的这两种写法都通用
    //df3.where('sdkversion === "Android 6.0").show()
    //df3.where("sdkversion = 'Android 6.0'").show()

    /*val df4 = df3.where("sdkversion = 'Android 6.0'")
    //覆盖写出json格式的文件
    df4.write.format("json").mode(SaveMode.Overwrite).save("cdcdata-spark-sql/out")*/

    //读取json嵌套json的文件
    val df5 = spark.read.format("json").load("cdcdata-spark-sql/data/per.json")
    df5.printSchema()
    //读取数据并取别名
    df5.select($"name",$"age",$"info.work".as("info-work"),$"info.home".as("info-home")).show()

  }

}
