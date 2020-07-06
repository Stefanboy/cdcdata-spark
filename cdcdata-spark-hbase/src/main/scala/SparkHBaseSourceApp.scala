import org.apache.spark.sql.SparkSession

/**
  * author：若泽数据-PK哥   
  * 交流群：545916944
  */
object SparkHBaseSourceApp {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName(this.getClass.getSimpleName)
      .master("local[2]")
      .getOrCreate()

    val df = spark.read.format("com.ruozedata.spark.source.hbase")
      .option("hbase.table.name","users")
      .option("spark.table.schema","(age int, name string)")
      .load()

    df.show()
//    df.createOrReplaceTempView("users")
//    spark.sql("select name,age,sex from users where age>30").show(false)

    spark.stop()
  }

}
