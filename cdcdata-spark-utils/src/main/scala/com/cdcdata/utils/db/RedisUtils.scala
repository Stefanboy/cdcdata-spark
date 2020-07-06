package com.cdcdata.utils.db

import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig}

object RedisUtils {

  private val poolConfig = new JedisPoolConfig
  poolConfig.setMaxTotal(2000)
  poolConfig.setMaxIdle(1000)
  poolConfig.setTestOnBorrow(true)

  private val pool = new JedisPool(poolConfig, "jd", 16379)
  def getJedis = pool.getResource

  def main(args: Array[String]): Unit = {
    println(getJedis)
    getJedis.get("ruoze")
  }
}
