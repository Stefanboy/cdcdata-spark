package com.cdcdata.spark.streaming.offset
import com.cdcdata.spark.streaming.kafka.StreamingKafkaMySQLApp07.Offset
import org.apache.kafka.common.TopicPartition
import org.apache.spark.streaming.kafka010.OffsetRange
import scalikejdbc.{DB, SQL}
import scalikejdbc.config.DBs

object MySQLOffsetsManager extends OffsetsManager {

  DBs.setup()

  override def obtainOffsets(topics: Array[String], groupId: String): Map[TopicPartition, Long] = {
    var fromOffsets = Map[TopicPartition, Long]()
    val offsets = DB.readOnly( implicit session => {
      SQL("select * from kafka_offset").map(rs => {
        (Offset(rs.string("topic"),rs.string("groupid"),rs.int("partitions"),rs.long("untiloffset")))
      }).list().apply()
    })
    offsets.foreach(x => {
      val topicPartition = new TopicPartition(topics(0),x.partitions.toInt)
      fromOffsets += topicPartition -> x.untiloffset.toLong
    })
    fromOffsets
  }

  override def storeOffsets(offsetRanges: Array[OffsetRange], groupId: String): Unit = {
    offsetRanges.foreach(x => {
      //输出每次消费的主题，分区，开始偏移量和结束偏移量
      println(s"---${x.topic},${x.partition},${x.fromOffset},${x.untilOffset}---")
      //将最新的偏移量信息保存到MySQL表中
      DB.autoCommit( implicit session => {
        SQL("replace into kafka_offset(topic,groupid,partitions,fromoffset,untiloffset) values (?,?,?,?,?)")
          .bind(x.topic,groupId,x.partition,x.fromOffset,x.untilOffset)
          .update().apply()
      })
    })
  }
}
