package com.cdcdata.spark.streaming.kafka

import com.cdcdata.utils.db.RedisUtils

/**
 * 测试pipeline
 */
object RedisPipelineApp {

  def main(args: Array[String]): Unit = {
    val jedis = RedisUtils.getJedis
    //切换数据库
    jedis.select(8)
    //获取pipeline
    val pipeline = jedis.pipelined()
    //使用pipeline必须要开启
    pipeline.multi()
    try {
      pipeline.hincrBy("user","aaa",30)
      //模拟异常
      //val a = 1/0
      pipeline.hincrBy("user","bbb",60)
      //执行
      pipeline.exec()
      //同步数据
      pipeline.sync()
      println("执行成功")
    } catch {
      case e:Exception =>{
        //执行失败将pipeline里面的东西丢掉
        pipeline.discard()
        e.printStackTrace()
      }
    } finally {
      pipeline.clear()
      jedis.close()
    }

  }

}
