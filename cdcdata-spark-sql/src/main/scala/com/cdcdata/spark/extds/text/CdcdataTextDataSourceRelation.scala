package com.cdcdata.spark.extds.text

import org.apache.spark.internal.Logging
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.sql.sources.{BaseRelation, TableScan}
import org.apache.spark.sql.types.{DoubleType, LongType, StringType, StructField, StructType}

/**
 * 自定义外部数据源 读取txt
 * @param sqlContext
 * @param path
 */
class CdcdataTextDataSourceRelation(val sqlContext:SQLContext,val path:String)
  extends BaseRelation with TableScan with Logging {
  //定义scheme
  override def schema: StructType = StructType(
    StructField("id",LongType,false)::
      StructField("name",StringType,false)::
      StructField("sex",StringType,false)::
      StructField("sal",DoubleType,false)::
      StructField("comm",DoubleType,false):: Nil
  )
  //定义buildScan 组装RDD[Row]
  override def buildScan(): RDD[Row] = {

    logError("这是cdc自定义数据源实现:buildScan")
    //通过spark拿到输入的内容 这是一个RDD[Row]
    val info = sqlContext.sparkContext.textFile(path)
    //拿到StructField
    val schemaFields: Array[StructField] = schema.fields

    //val value: RDD[Array[(String, Int)]] = info.map(_.split(",").map(_.trim)).map(x => x.zipWithIndex)

    info.map(_.split(",").map(_.trim))
      .map(x => x.zipWithIndex.map {
        case (value, index) => {
          val columnName = schemaFields(index).name
          Utils.castTo(if (columnName.equals("sex")) {
            if (value == "1") {
              "男"
            } else if (value == "2") {
              "女"
            } else {
              "未知"
            }
          } else {
            value
          }, schemaFields(index).dataType)

        }
      }).map(x => Row.fromSeq(x))

    //info.foreach(println)
    //null
  }
}
