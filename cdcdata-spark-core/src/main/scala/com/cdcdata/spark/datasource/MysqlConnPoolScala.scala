package com.cdcdata.spark.datasource

import java.sql.{Connection, ResultSet, Statement}
import java.util.Properties

import com.alibaba.druid.pool.DruidDataSourceFactory
import javax.sql.DataSource

object MysqlConnPoolScala {

  private val dataSource:DataSource = {
    val druidProps = new Properties()
    //获取Druid连接池的配置文件
    val druidConfig = getClass.getResourceAsStream("/db.properties")
    druidProps.load(druidConfig)
    DruidDataSourceFactory.createDataSource(druidProps)
  }

  //获取连接
  def getConnection:Connection = {
    dataSource.getConnection
  }
  //关闭
  def close(connection: Connection,statement: Statement,resultSet: ResultSet) = {
    if (resultSet != null) {
      resultSet.close()
    }
    if (statement != null) {
      statement.close()
    }
    if (connection != null) {
      connection.close()
    }

  }

}
