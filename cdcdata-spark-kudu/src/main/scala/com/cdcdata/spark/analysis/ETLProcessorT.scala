package com.cdcdata.spark.analysis

import com.cdcdata.spark.`trait`.ProcessorT
import com.cdcdata.spark.utils.ipv6.IPParser
import com.cdcdata.spark.utils.{ConfigUtils, ContantsSQL, ContantsSchema, KuduUtils}
import org.apache.kudu.spark.kudu.KuduContext
import org.apache.spark.sql.{SaveMode, SparkSession}


/**
 * ETL数据清洗类
 * 主要目的是只修改read 和write中间的业务逻辑一点都不需要动
 */
object ETLProcessorT extends ProcessorT {
  override def process(spark: SparkSession,kuduContext: KuduContext): Unit = {
    val json = spark.read.json("data/access.json")
    json.createOrReplaceTempView("ods")
    //取出ip的值 转为rdd
    val ips = json.select("ip").rdd.map(x => {
      //把ip当做String拿出来
      x.getAs[String]("ip")
    }).collect().toBuffer.toList //将ip放入到list中
    //解析ip
    val latitudeLongitudes = IPParser.parserIP(ips)
    //latitudeLongitudes.take(5).foreach(println)
    //普通的值如何转df  先转成rdd 然后to df
    //因为LatitudeLongitude是一个实体类 所以转换后的df能拿到scame信息
    import spark.implicits._
    val ipDF = spark.sparkContext.parallelize(latitudeLongitudes).toDF()
    ipDF.createOrReplaceTempView("latitude_longitude_country_city")
    //将解析的ip和原始表join起来
    val result = spark.sql(ContantsSQL.ODS_SQL)
    //result.select("longitude","latitude","country","city").show(false)
    /**
     * 将结果存储 如果这里没有设置coalesce重新分区 会生成200个文件 不能用repartition() 原因coalesce无shuffle repartition有shuffle
     * coalesce的大小=总的数据量/每个task处理的数据量
     *
     * 后面还有一种不用设置coalesce 就可以自动适配
     */
    result.coalesce(2).write.format(ConfigUtils.stroageFormat)
      .mode(SaveMode.Overwrite)
      .save(ConfigUtils.odsOutput)


    //将数据写到kudu 每次都这么写是不是重复了 考虑封装
    /*    result.write.format("org.apache.kudu.spark.kudu")
      .option("kudu.master","jd:7051")
      .option("kudu.table", "student")*/
    /**
     * 如果想把df/ds输出到kudu 需要的参数有
     * kuduContext
     * dataframe
     * kudutable
     * kudumaster
     * schema
     * partitionId
     *
     */
    //val schema = new Schema(null)
    val schema = ContantsSchema.ODS_SCHEMA
    KuduUtils.process(kuduContext, result,
      ConfigUtils.ods, ConfigUtils.kuduMaster,
      schema, "ip")

  }
}
