package com.cdcdata.spark.udf

import java.util

import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataType, DoubleType, IntegerType, LongType, StringType, StructField, StructType}
import org.apache.spark.sql.{Row, SaveMode, SparkSession}

import scala.collection.mutable.ListBuffer

object CdcdataUDFApp {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName(this.getClass.getSimpleName)
      .master("local")
      .getOrCreate()

    //udafWithSum(spark) //调用内置udf函数
    //udafAvgAgeWihSex(spark) //调用自定义udaf函数 多进一出
    udtf(spark) //调用自定义udtf函数 一进多出
    spark.stop()

  }

  /**
   * 涉及到行列转的 第一时间要想到flatmap函数 把东西拆开 然后做任意拼装就行了
   * @param spark
   */
  def udtf(spark:SparkSession)={
    //组装RDD[Roww]
    val rows = new util.ArrayList[Row]()
    rows.add(Row("ppp","Hive,Spark,Flink"))
    rows.add(Row("jj","CDH,Kafka,HBase"))
    //组装scheme
    val scheme = StructType(
      List(
        StructField("teachers",StringType,false),
        StructField("courses",StringType,false),

      )
    )
    //转换成df
    val df = spark.createDataFrame(rows, scheme)
    //注册成表
    df.createOrReplaceTempView("user")

    import spark.implicits._
    //实现自定义udtf函数
    df.flatMap(row =>{
      val list = new ListBuffer[(String, String)]()
      val courses = row.getString(1).split(",")
      for(course <- courses){
        list.append((row.getString(0),course))
      }
      list
    }).toDF("teacher","course").show()



  }


  /**
   * 调用自定义udaf
   * 该方法对应的sql是 select sex,cdcdata_avg(age) from xxx group by sex
   * @param spark
   */
  def udafAvgAgeWihSex(spark:SparkSession)={
    //组装RDD[Roww]
    val rows = new util.ArrayList[Row]()
    rows.add(Row("ppp",30,"M"))
    rows.add(Row("jj",18,"M"))
    rows.add(Row("sss",25,"W"))
    rows.add(Row("bbb",20,"W"))
    //组装scheme
    val scheme = StructType(
      List(
        StructField("name",StringType,false),
        StructField("age",IntegerType,false),
        StructField("sex",StringType,false),

      )
    )
    //转换成df
    val df = spark.createDataFrame(rows, scheme)
    //注册成表
    df.createOrReplaceTempView("user")
    //注册自定义udf函数
    spark.udf.register("cdcdata_avg_udaf",CdcdataAvgUDAF)

    spark.sql("select sex,cdcdata_avg_udaf(age) as age_avg from user group by sex").show(false)


  }

  /**
   * 自定义udaf类 必须实现UserDefinedAggregateFunction
   * 里面好多抽象方法，都得实现
   *
   */
  object CdcdataAvgUDAF extends UserDefinedAggregateFunction{
    //该方法表示udf函数要输入的数据类型 求avg = sum/参与计算的个数 所以只需要输入一个double类型的参数就行
    override def inputSchema: StructType = {
      StructType(
        StructField("nums",DoubleType,true)::Nil
      )
    }
    //该方法表示在聚合函数中参与计算数据的类型 avg = sum/参与计算的个数 所以在聚合函数中有两个计算类型sum和参与计算的个数
    override def bufferSchema: StructType = {
      StructType(
        StructField("buffer1",DoubleType,true):: //年龄总和 sum
          StructField("buffer2",LongType,true) //参与计算的人数
          ::Nil
      )
    }
    //表示udf函数返回的类型 即定义结果类型
    override def dataType: DataType = DoubleType
    //该方法放个true就行
    override def deterministic: Boolean = true
    //该方法表示在聚合函数中为输入的参数值赋默认值
    override def initialize(buffer: MutableAggregationBuffer): Unit = {
      buffer.update(0,0.0) //为第一个参数赋初始值
      buffer.update(1,0L)  //为第二个参数赋初始值
    }
    //分区内部聚合
    //该方法表示对输入的参数值进行操作 比如avg = sum/参与计算的个数 sum的话就是相加 参与计算的个数：每次加1就行了
    //buffer：函数已存在的数据 input:新输入的数据
    override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
      buffer.update(0,buffer.getDouble(0) + input.getDouble(0))
      buffer.update(1,buffer.getLong(1)+1)

    }
    //分区间聚合 不同分区的数据进行聚合 将结果重新放到buffer1中
    override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
      buffer1.update(0,buffer1.getDouble(0) + buffer2.getDouble(0))
      buffer1.update(1,buffer1.getLong(1) + buffer2.getLong(1))
    }

    //计算最终结果
    override def evaluate(buffer: Row): Any = {
      buffer.getDouble(0)/buffer.getLong(1)
    }
  }




  /**
   * 使用Spark内置的函数进行分组求和
   * @param spark
   */
  def udafWithSum(spark:SparkSession)={
    //组装RDD[Roww]
    val rows = new util.ArrayList[Row]()
    rows.add(Row("ppp",30,"M"))
    rows.add(Row("jj",18,"M"))
    rows.add(Row("sss",25,"W"))
    rows.add(Row("bbb",20,"W"))
    //组装scheme
    val scheme = StructType(
      List(
        StructField("name",StringType,false),
        StructField("age",IntegerType,false),
        StructField("sex",StringType,false),

      )
    )
    //转换成df
    val df = spark.createDataFrame(rows, scheme)
    //注册成表
    df.createOrReplaceTempView("user")
    //查询
    spark.sql("select sex,sum(age) from user group by sex").show(false)

  }

}
