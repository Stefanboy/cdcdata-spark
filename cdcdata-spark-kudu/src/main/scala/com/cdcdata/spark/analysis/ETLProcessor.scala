package com.cdcdata.spark.analysis

import com.cdcdata.spark.`trait`.Processor
import com.cdcdata.spark.utils.{ConfigUtils, ContantsSQL}
import com.cdcdata.spark.utils.ipv6.IPParser
import org.apache.spark.sql.{SaveMode, SparkSession}

/**
 * ETL数据清洗
 *
 */
object ETLProcessor extends Processor{
  override def process(spark: SparkSession): Unit = {
    //读取json数据
    val json = spark.read.json("cdcdata-spark-kudu/data/access.json")
    json.createOrReplaceTempView("ods")

    //取出ip的值 转为rdd 最后转为list
    val ips = json.select("ip").rdd.map(x => {
      //把ip当做String拿出来
      x.getAs[String]("ip")
    }).collect().toBuffer.toList

    //解析出ip并返回seq对象的集合
    val longitudes = IPParser.parserIP(ips)
    import spark.implicits._
    //将集合转为df
    //普通的值如何转df  先转成rdd 然后to df
    //因为LatitudeLongitude是一个实体类 所以转换后的df能拿到scame信息
    val ipDF = spark.sparkContext.parallelize(longitudes).toDF()
    ipDF.createOrReplaceTempView("latitude_longitude_country_city")
    //将解析的ip和原始表join起来
    val result = spark.sql(ContantsSQL.ODS_SQL)

    //将数据写出去
    /**
     * 将结果存储 如果这里没有设置coalesce重新分区 会生成200个文件 不能用repartition() 原因coalesce无shuffle repartition有shuffle
     * coalesce的大小=总的数据量/每个task处理的数据量
     *
     * 后面还有一种不用设置coalesce 就可以自动适配
     */
    result.coalesce(2).write.format(ConfigUtils.stroageFormat)
      .mode(SaveMode.Overwrite)
      .save(ConfigUtils.odsOutput)


  }
}
