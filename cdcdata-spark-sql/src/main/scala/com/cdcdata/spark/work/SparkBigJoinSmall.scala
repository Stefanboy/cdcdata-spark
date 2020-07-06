package com.cdcdata.spark.work

import org.apache.spark.sql.{SaveMode, SparkSession}
/**
 * spark-submit \
 * --class com.cdcdata.spark.work.SparkBigJoinSmall \
 * --name SparkBigJoinSmall \
 * --master yarn \
 * --executor-memory 3G \
 * /home/hadoop/lib/cdcdata-spark-sql-1.0.jar \
 * /cdcdata/input/user_click.txt /cdcdata/input/product.txt /cdcdata/output 1
 */
object SparkBigJoinSmall {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      //.master("local")
      //.appName(this.getClass.getName)
      .getOrCreate()

    //bigJoinSmall(spark)
    val (inputUser,inputProduct,output,flag) = (args(0),args(1),args(2),args(3))
    bigJoinSmallV2(spark,inputUser,inputProduct,output,flag)
    spark.stop()
  }

 /* def bigJoinSmall(spark:SparkSession): Unit ={
    val userRDD = spark.sparkContext.textFile("cdcdata-spark-sql/data/user_click.txt")
    val productRDD = spark.sparkContext.textFile("cdcdata-spark-sql/data/product.txt")
    val productMap = productRDD.map(x => {
      val split = x.split(",")
      (split(0), split(1))
    }).collectAsMap()
    val productBc = spark.sparkContext.broadcast(productMap)
    import spark.implicits._
    val df = userRDD.map(x => {
      val split = x.split(",")
      val userId = split(0).toInt
      val productId = split(1).toInt
      val categoryId = productBc.value.get(productId+"").get.toInt
      UserProduct(userId, productId, categoryId)
    }).toDF()
    //df.printSchema()
    //df.show()
    df.createOrReplaceTempView("userProduct")

    //使用spark快速求出用户点击过的商品类目
    //spark.sql("select distinct(categoryId)   from userProduct order by categoryId ").show()
    //使用spark求出每个类目点击量最大的50个商品
    //select categoryId,count(1) from userProduct group by categoryId

    val topNSQL =
      """select * from
        |(
        |select t.*,row_number() over(partition by categoryId order by cnt desc) as r
        |from
        |(select productId,categoryId,count(1) cnt from userProduct group by productId,categoryId) t
        |) a where a.r=1 limit 50""".stripMargin
    spark.sql(topNSQL).show()

  }*/

  case class UserProduct(id:Int,productId:Int,categoryId:Int)

  def bigJoinSmallV2(spark:SparkSession,inputUser:String,inputProduct:String,output:String,flag:String): Unit ={
    val userRDD = spark.sparkContext.textFile(inputUser)
    val productRDD = spark.sparkContext.textFile(inputProduct)
    val productMap = productRDD.map(x => {
      val split = x.split(",")
      (split(0), split(1))
    }).collectAsMap()
    val productBc = spark.sparkContext.broadcast(productMap)
    import spark.implicits._
    val df = userRDD.map(x => {
      val split = x.split(",")
      val userId = split(0).toInt
      val productId = split(1).toInt
      val categoryId = productBc.value.get(productId+"").get.toInt
      UserProduct(userId, productId, categoryId)
    }).toDF()
    //df.printSchema()
    //df.show()
    df.createOrReplaceTempView("userProduct")

    if(flag == "1"){
      //使用spark快速求出用户点击过的商品类目
      val result = spark.sql("select distinct(categoryId)   from userProduct order by categoryId ")
      result.write.format("parquet").mode(SaveMode.Overwrite).save(output)
    } else {

      val topNSQL =
        """select * from
          |(
          |select t.*,row_number() over(partition by categoryId order by cnt desc) as r
          |from
          |(select productId,categoryId,count(1) cnt from userProduct group by productId,categoryId) t
          |) a where a.r=1 limit 50""".stripMargin
      val result = spark.sql(topNSQL)
      result.write.format("parquet").mode(SaveMode.Overwrite).save(output)
    }


  }
}
