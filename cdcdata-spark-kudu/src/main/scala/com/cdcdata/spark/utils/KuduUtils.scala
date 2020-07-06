package com.cdcdata.spark.utils

import java.util
import java.util.LinkedList

import org.apache.kudu.Schema
import org.apache.kudu.client.CreateTableOptions
import org.apache.kudu.client.KuduClient.KuduClientBuilder
import org.apache.kudu.spark.kudu.KuduContext
import org.apache.spark.sql.{DataFrame, SaveMode}

/**
 * 将关于kudu的操作封装到类里面
 * 如果对hbase操作 也可以封装到类似HbaseUtils
 */
object KuduUtils {

  def process(kuduContext:KuduContext,
              df:DataFrame,
              kuduTable:String,
              kuduMaster:String,
              schema:Schema,
              partitionId:String): Unit = {
    val client = new KuduClientBuilder(kuduMaster).build()
    //判断表是否存在
    if(!kuduContext.tableExists(kuduTable)) {
      //这种新的写法 将方法体的最后一行赋给tableOptions
      val tableOptions = {
        val partitions = new util.LinkedList[String]
        //分区字段
        partitions.add(partitionId)
        //副本系数1 分区数3
        new CreateTableOptions().setNumReplicas(1)
          .addHashPartitions(partitions, 3)
      }
      //建表
      client.createTable(kuduTable, schema,tableOptions)
      //写数据
      df.write
          .mode(SaveMode.Append)//kudu只支持Append
        .format("org.apache.kudu.spark.kudu")
        .option("kudu.master", kuduMaster)
        .option("kudu.table", kuduTable)
        .save()
    }
  }
}
