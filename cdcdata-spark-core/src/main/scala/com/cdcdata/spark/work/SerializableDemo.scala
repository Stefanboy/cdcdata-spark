package com.cdcdata.spark.work

import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ArrayBuffer

object SerializableDemo {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()//.setAppName("SerializableDemo").setMaster("local")
    val sc = new SparkContext(conf)
    val arrayBuffer = new ArrayBuffer[Sturdent]()
    for(i <- 1 to 10000){
      arrayBuffer += Sturdent(i+"","aaa"+i,90)
    }
    val studentData = sc.parallelize(arrayBuffer)

    var flag = false;
    if(args.length > 0){
      flag= args(0).toBoolean
    }
    if(flag!=null && flag == true){
      studentData.persist(StorageLevel.MEMORY_ONLY_SER).collect()//序列化后放入内存 在ui中查看大小为 	270.0 KB
    } else {
      studentData.persist().collect()//默认MEMORY_ONLY 不序列化 在ui中查看大小为 1289.1 KB
    }
      Thread.sleep(10000)


    sc.stop()
  }
}
case class Sturdent(id:String,name:String,age:Int)
