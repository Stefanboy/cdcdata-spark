package com.cdcdata.spark.streaming.state

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, State, StateSpec, StreamingContext}

/**
 * 让stream有状态
 * 之前没有状态的话只能对进来的这一批次做操作 有状态的话可以对该批次和以前的批次做操作
 * 比如求当天到现在为止的热点新闻
 *
 * 该版本在程序重新启动时就能读到原来的批次信息了 自定义函数也有key 最终版
 */
object StateApp03 {
  def main(args: Array[String]): Unit = {


    val checkpoint = "cdcdata-spark-streaming/state/state-chk"
    def functionToCreateContext:StreamingContext = {
      val sparkConf = new SparkConf().setMaster("local[2]").setAppName(this.getClass.getName)
      val ssc = new StreamingContext(sparkConf, Seconds(5))
      val stream = ssc.socketTextStream("jd", 9998)
      ssc.checkpoint(checkpoint)

      stream.flatMap(_.split(","))
        .map((_,1)).mapWithState(StateSpec.function(func).timeout(Seconds(3000)))
        .print()
      ssc
    }

    val ssc = StreamingContext.getOrCreate(checkpoint, functionToCreateContext _)




    ssc.start()
    ssc.awaitTermination()
  }

  //有可能当前批次的key在之前批次没有 所以getOrElse
  //option：当前批次的某一元素的value值 preValues：之前批次的全部信息
  //该函数是进来一条数据执行一次 所以要想取最终结果，要取key的最大值
  val func = (word:String, option:Option[Int], state:State[Int]) => {
    //增强程序的健壮性
    if (state.isTimingOut()){
      //...
    } else {
      val sum = option.getOrElse(0) + state.getOption().getOrElse(0)
      val result =(word,sum)
      state.update(sum)
      result
    }
  }


}
