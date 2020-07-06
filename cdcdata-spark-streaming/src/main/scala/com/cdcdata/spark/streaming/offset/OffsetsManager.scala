package com.cdcdata.spark.streaming.offset

import org.apache.kafka.common.TopicPartition
import org.apache.spark.streaming.kafka010.OffsetRange

trait OffsetsManager {
  //获取Map[TopicPartition, Long]
  def obtainOffsets(topics:Array[String],groupId:String):Map[TopicPartition, Long]
  //存offsets
  def storeOffsets(offsetRanges: Array[OffsetRange],groupId:String)

}
