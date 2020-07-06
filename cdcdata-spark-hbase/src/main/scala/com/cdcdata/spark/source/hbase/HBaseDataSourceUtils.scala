package com.cdcdata.spark.source.hbase

object HBaseDataSourceUtils {

  def extractSparkFields(sparkTableScheme:String):Array[SparkSchema] ={
    val columns = sparkTableScheme.trim.drop(1).dropRight(1).split(",")
    val sparkSchemas = columns.map(x => {
      val splits = x.trim.split(" ")
      SparkSchema(splits(0),splits(1))
    })
    sparkSchemas
  }

  def main(args: Array[String]): Unit = {
    val sparkTableSchema="age int, name string, sex string"
    val columns = sparkTableSchema.trim.drop(1).dropRight(1).split(",")
    columns.map(x => {
      val splits = x.trim.split(" ")
      SparkSchema(splits(0),splits(1))
    }).foreach(println)
  }



}
