package com.cdcdata.spark.streaming.utils

import java.util.{Properties, Random}

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.slf4j.{Logger, LoggerFactory}

/**
 * 生产数据到kafka
 */
object RandomWordGenerator {

  private val log: Logger = LoggerFactory.getLogger(RandomWordGenerator.getClass)

  def main(args: Array[String]): Unit = {

    val props = new Properties()
    //生产到的地址
    props.put("bootstrap.servers", "jd:9092,jd:9093,jd:9094");
    props.put("acks", "all");
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

    val producer = new KafkaProducer[String, String](props)


    for(i <- 1 to 3){

      Thread.sleep(100)

      val part = 1%3
      //val word = String.valueOf((new Random().nextInt(6) + 'a').toChar)
      val word = "1,100,1"
      log.error(word)
      val record = new ProducerRecord[String, String]("cdcdata3partstopic",part,"",word)
      producer.send(record)
    }

  }
}
