package com.cdcdata.spark.rdd2df

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.{Row, SparkSession}

/**
 * rdd转df的两种方式
 * 1） reflection   case class 在2.2之前好像最大只能支持22个属性
 * 2） programmatic
 * RDD[String] ==> RDD[Row]
 * schema
 * StructType
 * StructField
 */
object DataFrameRDDApp {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .master("local")
      .appName(this.getClass.getSimpleName)
      .getOrCreate()

    reflection(spark)
    //programmatic(spark)

    spark.stop()


  }

  def programmatic(spark: SparkSession): Unit = {
    //rdd->df用到的隐式转换
    //RDD[String] ==> RDD[Row]
    val rdd = spark.sparkContext.textFile("cdcdata-spark-sql/data/info.txt")
    val infoRDD: RDD[Row] = rdd.map(x => {
      val split = x.split(",")
      val id = split(0).trim.toInt
      val name = split(1).trim
      val age = split(2).trim.toInt
      Row(id, name, age)
    })
    //schema (StructType StructField)
    val schema =
      StructType(
        StructField("id", IntegerType, true) ::
          StructField("name", StringType, false) ::
          StructField("age", IntegerType, false) :: Nil)
    // createDataFrame

    val df = spark.createDataFrame(infoRDD, schema)
    df.printSchema()
    df.show()
  }

  /**
   * 使用反射方式将RDD转成DF
   */
  def reflection(spark: SparkSession): Unit = {
    import spark.implicits._
    //创建RDD
    val rdd = spark.sparkContext.textFile("file:/E:/bigdata/workSpace/cdcdata-spark/cdcdata-spark-sql/data/info.txt")
    //转成caseclass
    val df = rdd.map(x => {
      val split = x.split(",")
      val id = split(0).trim.toInt
      val name = split(1).trim
      val age = split(2).trim.toInt
      Info(id, name, age)
    }).toDF()
    df.printSchema()
    df.show()

  }

  case class Info(id: Int, name: String, age: Int)

}
