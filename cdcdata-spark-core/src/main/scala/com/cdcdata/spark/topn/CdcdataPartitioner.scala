package com.cdcdata.spark.topn

import org.apache.spark.Partitioner

import scala.collection.mutable

/**
 * 自定义分区器
 */
class CdcdataPartitioner(domains:Array[String]) extends Partitioner{
  //定义分区规则
  val map = mutable.HashMap[String,Int]()
  for(i <- 0 until(domains.length)){
    map(domains(i)) = i
  }
  //重写控制分区数量的方法
  override def numPartitions: Int = {
    domains.length
  }

  //控制分区规则的方法
  override def getPartition(key: Any): Int = {
    val domain = key.asInstanceOf[(String, String)]._1
    map(domain)
  }
}
