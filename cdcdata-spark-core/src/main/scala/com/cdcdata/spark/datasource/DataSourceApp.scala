package com.cdcdata.spark.datasource

import java.sql.{Connection, DriverManager, PreparedStatement}

import com.cdcdata.spark.utils.FileUtils
import org.apache.hadoop.io.compress.BZip2Codec
import org.apache.spark.rdd.JdbcRDD
import org.apache.spark.{SparkConf, SparkContext}

object DataSourceApp {
  def main(args: Array[String]): Unit = {
    //在启动前将用户设置进去
    System.setProperty("HADOOP_USER_NAME","hadoop")
    val conf = new SparkConf().setMaster("local[2]").setAppName("master")
    val sc = new SparkContext(conf)
    //写本地
    saveTextFile(sc)
    //写hdfs
    saveHDFSTextFile(sc)
    saveObjectFile(sc)
    readObjectFile(sc)
    sc.stop()
  }

  //读数据
  def readObjectFile(sc:SparkContext): Unit ={

    sc.objectFile[Person]("out").collect().foreach(println)
  }

  //读mysql数据
  def readMysql(sc:SparkContext): Unit ={
    val person: JdbcRDD[(String, Int)] = new JdbcRDD(sc, () => {
      DriverManager.getConnection("jdbc:mysql//jd:3306/", "root", "mysqladmin")
    },
      "select * from person where age>=? and age<=?"
      , 70, 110, 1,
      resultSet => (resultSet.getString(1), resultSet.getInt(2))
    )
    println("count:" +person.count())
    person.collect().foreach(println)
  }

  //写到mysql
  def saveMySQL(sc:SparkContext): Unit ={
    val rdd = sc.parallelize(Array(("BJ",100),("SH",80),("SZ",60)),2)
    rdd.foreach(x=> {
      val connection = DriverManager.getConnection("jdbc:mysql//jd:3306/", "root", "mysqladmin")
      val statement = connection.prepareStatement("insert into table values(?,?)")
      statement.setString(1, x._1)
      statement.setInt(2, x._2)
      statement.execute()
      connection.close()
    })
  }

  //写到mysql V2版
  def saveMySQLV2(sc:SparkContext): Unit ={
    val rdd = sc.parallelize(Array(("BJ",100),("SH",80),("SZ",60)),2)
    rdd.foreachPartition(partition=> {
      val connection = DriverManager.getConnection("jdbc:mysql//jd:3306/", "root", "mysqladmin")
      println("------------")
      connection.setAutoCommit(false)
      val statement = connection.prepareStatement("insert into table values(?,?)")
      partition.foreach(x =>{
        statement.setString(1, x._1)
        statement.setInt(2, x._2)
        statement.addBatch()
      })
      statement.executeBatch()
      connection.commit()
      connection.close()
    })

    var pstmt: PreparedStatement = null
    var connection: Connection = null
    try{
      rdd.foreachPartition(partition => {
        connection = DriverManager.getConnection("url", "user", "password")
        connection.setAutoCommit(false)
        pstmt = connection.prepareStatement("sql")
        //放入对应元素
        partition.foreach(x => {
          pstmt.setString(1, x._1)
          pstmt.setInt(2, x._2)
          pstmt.addBatch()
        })
        //批量提交
        pstmt.executeBatch()
        connection.commit()

        connection.close()
      })

    } catch {
      case e:Exception =>e.printStackTrace()
    } finally {
      if(pstmt != null) pstmt.close()
      if(connection != null) connection.close()

    }



  }

  //写数据 写对象的话 文件里的东西是看不出啥东西来的
  def saveObjectFile(sc:SparkContext): Unit ={
    val p1 = Person("aaa",18)
    val p2 = Person("bbb",20)
    val out = "out"
    sc.parallelize(List(p1,p2)).saveAsObjectFile(out)
  }

  def saveTextFile(sc:SparkContext): Unit ={
    val rdd = sc.parallelize(Array(("PK",30),("xing",18),("JJ",60)))
    //把rdd以文本的形式写出去
    val out = "out"
    FileUtils.delete(sc.hadoopConfiguration,out);
    //看源码 其实就是使用mapreduce中的context(k,v)写出去
    rdd.saveAsTextFile(out)
  }


  def saveHDFSTextFile(sc:SparkContext): Unit ={
    sc.hadoopConfiguration.set("fs.defaultFS","hdfs://jd:9000")
    sc.hadoopConfiguration.set("dfs.client.use.datanode.hostname","true")
    val rdd = sc.parallelize(Array(("PK",30),("xing",18),("JJ",60)))
    //把rdd以文本的形式写出去
    val out = "out"
    FileUtils.delete(sc.hadoopConfiguration,out);
    //看源码 其实就是使用mapreduce中的context(k,v)写出去
    rdd.saveAsTextFile(out)
    //如果输出文件是压缩的，指定压缩格式
    //看源码 点进去saveAsHadoopFile一直到PairRDDFunctions,看到其实跟mapreduce输出差不多的
    //rdd.saveAsTextFile(out,classOf[BZip2Codec])
  }
  case class Person(name:String,age:Int)
}
