import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ListBuffer

object TestApp {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("master")
    val sc = new SparkContext(sparkConf)

    sc.stop()
    //testTime()
  }


  def testTime(): Unit ={
    val time1 = "12:00:00"
    val time2 = "12:00:01"
    val i = time1.compareTo(time2)
    println(i)

  }

}
