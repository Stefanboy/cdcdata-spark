package com.cdcdata.spark.extds.text

import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.sources.{BaseRelation, RelationProvider}

/**
 * 该类的名字只能叫DefaultSource 不能写其他的 ，因为源码默认就是找DefaultSource类名 是规定
 */
class DefaultSource extends RelationProvider{
  //parameters表示option的参数
  override def createRelation(sqlContext: SQLContext, parameters: Map[String, String]): BaseRelation = {
    val path = parameters.get("path")
    path match {
      case Some(p) => new CdcdataTextDataSourceRelation(sqlContext,p)
      case _ => throw new IllegalArgumentException("path is required")
    }
  }
}
