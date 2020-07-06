package com.cdcdata.spark.streaming.offset
import java.util

import com.cdcdata.utils.db.RedisUtils
import org.apache.kafka.common.TopicPartition
import org.apache.spark.streaming.kafka010.OffsetRange
import redis.clients.jedis.{Jedis, Pipeline}

object RedisOffsetsManager extends OffsetsManager {

  //val jedis = RedisUtils.getJedis
  var jedis: Jedis = null
  var pipeline: Pipeline = null
  //获取上次消费的offset
  override def obtainOffsets(topics: Array[String], groupId: String): Map[TopicPartition, Long] = {
    var fromOffsets = Map[TopicPartition, Long]()
    //获取连接
    jedis = RedisUtils.getJedis
    //这是java类型的数据，导入隐式转换 转为scala类型的数据
    val offsets: util.Map[String, String] = jedis.hgetAll(topics(0) + "_" + groupId)
    import scala.collection.JavaConversions._
    offsets.map(x => {
      fromOffsets += new TopicPartition(topics(0),x._1.toInt) -> x._2.toLong
    })
    fromOffsets
  }
  //提交offset
  override def storeOffsets(offsetRanges: Array[OffsetRange], groupId: String): Unit = {
    pipeline = jedis.pipelined()
    offsetRanges.foreach(x => {
      pipeline.hset(x.topic+"_"+groupId,x.partition+"",x.untilOffset+"")
    })
  }
}
