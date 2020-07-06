package com.cdcdata.spark.work

import java.text.SimpleDateFormat

import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ListBuffer

/**
 * 1、Spark实现：
 *
 * product_no lac_id moment start_time user_id county_id staytime city_id
 * 13429100031	22554	8	2013-03-11 08:55:19.151754088	571	571	282	571
 * 13429100082	22540	8	2013-03-11 08:58:20.152622488	571	571	270	571
 * 13429100082	22691	8	2013-03-11 08:56:37.149593624	571	571	103	571
 * 13429100087	22705	8	2013-03-11 08:56:51.139539816	571	571	220	571
 * 13429100087	22540	8	2013-03-11 08:55:45.150276800	571	571	66	571
 * 13429100082	22540	8	2013-03-11 08:55:38.140225200	571	571	133	571
 * 13429100140	26642	9	2013-03-11 09:02:19.151754088	571	571	18	571
 * 13429100082	22691	8	2013-03-11 08:57:32.151754088	571	571	287	571
 * 13429100189	22558	8	2013-03-11 08:56:24.139539816	571	571	48	571
 * 13429100349	22503	8	2013-03-11 08:54:30.152622440	571	571	211	571
 *
 * 字段解释
 * product_no:用户手机号：lac_id：用户所在基站：start_time：用户在此基站的开始时间：
 *
 * staytime：用户在此基站的逗留时间。
 *
 * 需求描述：
 * 根据lac_id和start_time知道用户当时的位置，根据staytime知道用户各个基站的逗留时长，根据轨迹结合并连续基站的staytime。最终得到每一个用户按时间排序在每一个基站驻留时长。
 * 期望输出举例：
 *
 * 13429100082 22540 8 2013-03-11 08:58:20.152622488 571 571 270 571
 * 13429100082 22691 8 2013-03-11 08:56:37.149593624 571 571 390 571
 * 13429100082 22540 8 2013-03-11 08:55:38.140225200 571 571 133 571
 * 13429100087 22705 8 2013-03-11 08:56:51.139539816 571 571 220 571
 * 13429100087 22540 8 2013-03-11 08:55:45.150276800 571 571 66 571
 * groupby 手机号，然后再array里面做排序合并
 */
object BaseStationApp {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("baseStation").setMaster("local")
    val sc = new SparkContext(conf)
    val value = sc.textFile("file:///E:/bigdata/workSpace/cdcdata-spark/cdcdata-spark-core/data/baseStation.txt")
    val groupByResult = value.map(x => {
      val split = x.split(",")
      val productNo = split(0).toLong
      val lacId = split(1)
      val moment = split(2)
      val oldstartTimeStr = split(3)
      val startTimeStr = split(3).substring(0, split(3).indexOf("."))
      val fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      val startTimeDate = fm.parse(startTimeStr)
      val startTime = startTimeDate.getTime
      val userId = split(4)
      val countyId = split(5)
      val staytime = split(6).toInt
      val cityId = split(7)
      (productNo, lacId, moment, startTime, oldstartTimeStr, userId, countyId, staytime, cityId)
    }).sortBy(x => (x._1, -x._4)).map(x => (x._1, (x))).groupByKey()

    groupByResult.map(x => {
      val value1 = x._2
      val list: List[(Long, String, String, Long, String, String, String, Int, String)] = value1.toList
      val buffer = ListBuffer[(Long, String, String, Long, String, String, String, Int, String)]()
      var flag = false
      for(i <- 0 to list.length-1){
        if(list.length == 1){
          buffer ++= list
          flag = true
        }
        if(!flag){
          if(i == list.length-1){
            buffer += list(i)

          } else {
            if((list(i)._1+list(i)._2) == (list(i+1)._1+list(i+1)._2) && list(i)._4 > list(i+1)._4){
              val tuple = (list(i+1)._1,list(i+1)._2,list(i+1)._3,list(i+1)._4,list(i+1)._5,list(i+1)._6,list(i+1)._7,list(i)._8+list(i+1)._8,list(i+1)._9)
              buffer += tuple
            } else if (i != 0 && (list(i-1)._1+list(i-1)._2) == (list(i)._1+list(i)._2)){
              println("相邻元素相等")
            } else {
              buffer +=list(i)
            }
          }
        }
      }

        buffer.toList

    }).map(x =>x.map(y =>(y._1,y._2,y._3,y._5,y._6,y._7,y._8,y._9))).collect().foreach(println)
    sc.stop()
  }

}
//case class BaseStation(productNo:String,lacId:String,moment:String,startTime:String,userId:String,countyId:String,staytime:String,cityId:String)

case class BaseStation2(productNo:Long,lacId:String,moment:String,startTime:Long,oldstartTimeStr:String,userId:String,countyId:String,staytime:Int,cityId:String)extends Ordered[BaseStation2]{
  override def toString: String = {
    productNo+","+lacId+","+moment+","+oldstartTimeStr+","+userId+","+countyId+","+staytime+","+cityId
  }
  override def compare(that: BaseStation2): Int = {
    (this.productNo-that.productNo).toInt
    /*if( this.productNo-that.productNo != 0){
      (this.productNo-that.productNo).toInt
    }else{
      (this.startTime - that.startTime).toInt
    }*/

  }
}