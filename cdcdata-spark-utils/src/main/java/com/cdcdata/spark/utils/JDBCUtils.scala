package com.cdcdata.spark.utils

import java.sql.DriverManager

import org.apache.spark.SparkContext

import scala.collection.mutable.ListBuffer

class JDBCUtils {
  val connection = DriverManager.getConnection("jdbc:mysql://jd:3306/bigdata", "root", "mysqladmin")

  def saveMySQL(result:String): Unit = {

      println("-----------------------------")
      val pstmt = connection.prepareStatement("insert into t_listen (data) values (?)")
      pstmt.setString(1, result)
      pstmt.execute()

  }
  //connection.close()



}