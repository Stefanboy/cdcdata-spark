package com.cdcdata.spark.work

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.storage.StorageLevel

import scala.collection.mutable.ArrayBuffer

object KryoSerializableDemo {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("SerializableDemo")
      .setMaster("local[2]")
      .set("spark.serializer","org.apache.spark.serializer.KryoSerializer")
      //.registerKryoClasses(Array(classOf[Sturdent])) // 将自定义的类注册到Kryo
    val sc = new SparkContext(conf)
    val arrayBuffer = new ArrayBuffer[Sturdent]()
    for(i <- 1 to 10000){
      arrayBuffer += Sturdent(i+"","aaa"+i,90)
    }
    val studentData = sc.parallelize(arrayBuffer)
    studentData.persist(StorageLevel.MEMORY_ONLY_SER).collect()//Kryo方式未注册序列化后放入内存 476.4 KB
    Thread.sleep(10000)

    sc.stop()
  }

}