package com.cdcdata.spark.datasource

import java.sql.{Connection, DriverManager, PreparedStatement}

import org.apache.spark.{SparkConf, SparkContext}

object Source2MySQL {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[2]").setAppName("master")
    val sc = new SparkContext(conf)
    saveMySQLV2(sc)
    sc.stop()
  }

  //写到mysql V2版
  def saveMySQLV2(sc:SparkContext): Unit ={
    val rdd = sc.parallelize(Array(("BJ",100),("SH",80),("SZ",60)),2)
    var statement:PreparedStatement = null
  /*  rdd.foreachPartition(partition=> {
      var connection:Connection = null
      connection = MysqlConnPoolScala.getConnection
      println("------------")
      connection.setAutoCommit(false)
      statement = connection.prepareStatement("insert into table values(?,?)")
      partition.foreach(x =>{
        statement.setString(1, x._1)
        statement.setInt(2, x._2)
        statement.addBatch()
      })
      statement.executeBatch()
      connection.commit()
      connection.close()
    })
*/
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


}
