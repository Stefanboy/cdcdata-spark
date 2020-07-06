package com.cdcdata.spark.streaming.foreach

import java.sql.{Connection, DriverManager}

/**
 * mysql连接 关闭工具类
 */
object MySQLUtils {

  def getConnection()={
    Class.forName("com.mysql.jdbc.Driver")
    DriverManager.getConnection("jdbc:mysql://jd:3306/bigdata","root","mysqladmin")
  }

  def closeConnection(connection:Connection): Unit ={
    if(null != connection){
      connection.close()
    }
  }

}
