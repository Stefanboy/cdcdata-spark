package com.cdcdata.spark.source.hbase

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.sql.sources.{BaseRelation, TableScan}
import org.apache.spark.sql.types.StructType

case class HBaseRelation(@transient val parameters:Map[String,String])
                   (@transient val sqlContext:SQLContext)
  extends BaseRelation with TableScan{



  override def schema: StructType = {

  }

  override def buildScan(): RDD[Row] = {

  }
}
