package com.cdcdata.spark.strengthen

import org.apache.hadoop.io.NullWritable
import org.apache.hadoop.mapred.lib.MultipleTextOutputFormat

class CdcdataMultipleTextOutputFormat extends MultipleTextOutputFormat[Any,Any]{

  //输出的key
  override def generateActualKey(key: Any, value: Any): Any = {
    NullWritable.get()
  }

  //输出的value
  override def generateActualValue(key: Any, value: Any): Any = {
    value.asInstanceOf[String]
  }

  //输出文件的格式
  override def generateFileNameForKeyValue(key: Any, value: Any, name: String): String = {
    s"$key/$name"
    //按系统名/日期 格式输出文件
    //s"$key/20281022"
  }

}
