package com.cdcdata.spark.strengthen

import com.cdcdata.spark.utils.FileUtils
import org.apache.spark.{SparkConf, SparkContext}

/**
 *需求：按照操作系统多目录输出
 * 点击有多个参数的saveAsTextFile()的源码，方法里其实最后调的saveAsHadoopFile方法，
 * 那我们可以直接调用saveAsHadoopFile()方法，看该方法的参数，我们可以指定输出，点进去源码，
 * 1026行
 * 我们自定义输出类：继承MultipleTextOutputFormat，里面泛型不知道是什么，可以写上any，
 * 自定义输出类，如果不知道里面参数是啥意思，就走走debug看看
 * 比如自定义输出文件CdcdataMultipleTextOutputFormat中的generateFileNameForKeyValue方法就是控制文件输出格式的方法
 *
 */
object Work03App {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName(this.getClass.getSimpleName)
    val sc = new SparkContext(sparkConf)
    val out = "cdcdata-spark-core/out"
    FileUtils.delete(sc.hadoopConfiguration, out)
    sc.textFile("cdcdata-spark-core/data/phone.log",1)
      .map(x =>{
        val splits = x.split("\t")
        (splits(1),x)
      }).saveAsHadoopFile(out,classOf[String],classOf[String],classOf[CdcdataMultipleTextOutputFormat])


    sc.stop()
  }

}
