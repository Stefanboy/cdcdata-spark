package com.cdcdata.spark.action

import org.apache.spark.{SparkConf, SparkContext}

/**
 * 之前的transformation算子只会创建RDD而已，不会提交job
 * 只有sc.runJob  ==> 才会真正提交作业到Spark上运行
 */
object ActionApp {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[2]").setAppName("master")
    val sc = new SparkContext(conf)
    val rdd = sc.parallelize(List(1, 2, 3, 4))
    //看源码，介绍和注意事项，返回一个包括所有元素的数组，只能用于比较少的数据，因为会加载到diver的内存里
    //生产上不会这么弄，如果一下子10亿条数据，会oom，真想看数据的话可以使用take或者存到hdfs
    rdd.collect() //结果 res0: Array[Int] = Array(1, 2, 3, 4)
    //返回第一个元素 看源码 其实调用的是take函数
    rdd.first() //结果 res2: Int = 1
    //返回前n的元素的数组，不经过排序
    rdd.take(2) //结果 res3: Array[Int] = Array(1, 2)
    //返回rdd里元素的个数 如果没有参数 ()可以去掉
    rdd.count() //结果 res4: Long = 4
    rdd.sum() //结果 res5: Double = 10.0
    rdd.max() //结果 res6: Int = 4
    rdd.min() //结果 res7: Int = 1
    //做排序，降序取前n个 如果是字符串就按字典序降序取 看源码 里面应用了柯里化 介绍和样例 当看到返回Array时，就要注意数据量不能太大

    //Ordering 继承了compare 里面有比较方法 看源码 本质还是调用takeOrdered
    rdd.top(2) //结果 res8: Array[Int] = Array(4, 3)
    //如果要求升序怎么办
    rdd.top(2)(Ordering.by(x => -x)) //结果 res9: Array[Int] = Array(1, 2)
    //升序也可以使用这个
    rdd.takeOrdered(2) //结果 res10: Array[Int] = Array(1, 2)
    //相邻的元素相加
    rdd.reduce(_+_) //结果 res11: Int = 10

    //循环
    rdd.foreach(println) //结果 1 2 3 4
    //countByKey 根据key算出现的个数 用于看数据的分布 只能用于kv类型的 为什么是一个action  看源码里面有一个collect
    rdd.map(x => (x,1)).countByKey() //结果 res13: scala.collection.Map[Int,Long] = Map(4 -> 1, 2 -> 1, 1 -> 1, 3 -> 1)
    //把key相同的value的东西都放一块
    val rdd2 = sc.parallelize(List(("a","1"),("a","2"),("b","1")))
    rdd2.lookup("a") //结果 res14: Seq[String] = WrappedArray(1, 2)




    sc.stop()

  }

}
