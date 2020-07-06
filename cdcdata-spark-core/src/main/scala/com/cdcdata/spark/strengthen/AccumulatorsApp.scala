package com.cdcdata.spark.strengthen

import org.apache.spark.{SparkConf, SparkContext}

/**
 * spark中的计数器
 * 该计数器需要注意的是，遇到一次action就执行一次，所以会导致计数器不对，在写代码的时候要注意，
 * 看是否需要调整代码顺序，不要写多个action
 */
object AccumulatorsApp {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName(this.getClass.getSimpleName)
    val sc = new SparkContext(sparkConf)

    //计数器举例1
/*    val rdd = sc.parallelize(List(1, 2, 3, 4, 5, 6, 7, 8))
    val cnts = sc.longAccumulator("cnts")
    rdd.foreach(x =>{
      cnts.add(1L)
    })
    println(cnts.value)*/

    //计数器举例2
/*    val rdd = sc.parallelize(Array(("ruoze", 30), ("pk", 28), ("xingxing", 60), ("J", 16)))
    val acc = sc.longAccumulator("已成年的人数")
    val rdd2 =rdd.map(x => {
      if (x._2 >= 18) {
        acc.add(1L)
      }
    })
    rdd2.collect()
    //第一次执行action acc 3 第二次执行action acc 6
    println(acc.value)*/

    //计数器举例3 集合计数器
    //求id的后三位相同的user
    val users = Array(
      User("pk","8000000"),
      User("J","8000001"),
      User("xx","8000222"),
      User("ruoze","8000003")
    )
    val rdd = sc.parallelize(users)
    val acc = sc.collectionAccumulator[User]("test")
    rdd.foreach(user =>{
      val id = user.id.reverse
      if(id(0) == id(1) && id(0) == id(2)){
        acc.add(user)
      }
    })
    //放入缓存 lazy 得有action才会执行
    rdd.cache()
    rdd.persist()
    //删除缓存 立即执行 不是lazy
    rdd.unpersist(true)


    sc.stop()
  }

  case class User(name:String,id:String)
}
