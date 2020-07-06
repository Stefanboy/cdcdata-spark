package com.cdcdata.spark.streaming.transform

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * 使用transform算子将DStream转换成RDD并与其他RDD做join操作
 * 达到过滤黑名单的功能
 */
object TransformApp02 {
  def main(args: Array[String]): Unit = {


    val checkpoint = "cdcdata-spark-streaming/state/transform-checkpoint"
    def functionToCreateContext:StreamingContext = {
      val sparkConf = new SparkConf().setMaster("local[2]").setAppName(this.getClass.getName)
      val ssc = new StreamingContext(sparkConf, Seconds(5))
      ssc.checkpoint(checkpoint)

      val blacks = List("xingxing")
      val blacksRDD = ssc.sparkContext.parallelize(blacks).map((_, true))
      //输入进来的数据格式 (pk,30)
      val stream = ssc.socketTextStream("jd", 9998)
      //转换格式为 (pk,(pk,30))
      stream.map(x =>(x.split(",")(0),x))
          .transform(rdd => {
            rdd.leftOuterJoin(blacksRDD)
            .filter(x => {
              x._2._2.getOrElse(false) != true
            }).map(_._2._1)
          }).print()

      ssc
    }

    val ssc = StreamingContext.getOrCreate(checkpoint, functionToCreateContext _)




    ssc.start()
    ssc.awaitTermination()
  }

}
