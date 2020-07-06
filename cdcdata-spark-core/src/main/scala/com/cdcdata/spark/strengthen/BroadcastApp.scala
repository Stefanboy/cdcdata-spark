package com.cdcdata.spark.strengthen

import org.apache.spark.{SparkConf, SparkContext}

object BroadcastApp {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName(this.getClass.getSimpleName)
    val sc = new SparkContext(sparkConf)
    // Fact table  航线(起点机场, 终点机场, 航空公司, 起飞时间)
    val flights = sc.parallelize(List(
      ("SEA", "JFK", "DL",  "7:00"),
      ("SFO", "LAX", "AA",  "7:05"),
      ("SFO", "JFK", "VX", "7:05"),
      ("JFK", "LAX", "DL", "7:10"),
      ("LAX", "SEA", "DL",  "7:10")))

    // Dimension table 机场(简称, 全称, 城市, 所处城市简称)
    val airports = sc.parallelize(List(
      ("JFK", "John F. Kennedy International Airport", "New York", "NY"),
      ("LAX", "Los Angeles International Airport", "Los Angeles", "CA"),
      ("SEA", "Seattle-Tacoma International Airport", "Seattle", "WA"),
      ("SFO", "San Francisco International Airport", "San Francisco", "CA")))

    // Dimension table  航空公司(简称,全称)
    val airlines = sc.parallelize(List(
      ("AA", "American Airlines"),
      ("DL", "Delta Airlines"),
      ("VX", "Virgin America")))

    //最终统计结果：
    //出发城市           终点城市           航空公司名称         起飞时间
    //Seattle           New York       Delta Airlines           7:00
    //San Francisco     Los Angeles    American Airlines       7:05
    //San Francisco     New York       Virgin America            7:05
    //New York          Los Angeles    Delta Airlines           7:10
    //Los Angeles       Seattle        Delta Airlines          7:10
    //将airlines转换成map存到缓存中
    val airlinesBc = sc.broadcast(airlines.collectAsMap())
    //将airports取出第1和3数据放入缓存中
    val airportsBc = sc.broadcast(airports.map(x => (x._1, x._3)).collectAsMap())
    flights.map{
      case (a,b,c,d) => (airportsBc.value.get(a).get,
        airportsBc.value.get(b).get,
        airlinesBc.value.get(c).get,
        d)
    }.foreach(println)
    sc.stop()
  }

}
