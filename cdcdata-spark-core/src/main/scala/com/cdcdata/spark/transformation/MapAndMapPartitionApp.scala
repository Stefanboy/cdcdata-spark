package com.cdcdata.spark.transformation

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ListBuffer

/**
 *
 * 遇到xxxbyKey 就要想到数据是kv类型的
 * cogroup跟join的区别是:cogroup的结果类型是可迭代的
 * groupByKey()和reduceByKey()的区别:reduceByKey()在分区中有一次预聚合
 * repartition和coalesce：coalesce应用于将分区变小，repartition将分区变多
 * 排序：隐式转换的排序 在隐式转换里讲的 最简单的方式  上一课讲的
 */
object MapAndMapPartitionApp {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[2]").setAppName("master")
    val sc = new SparkContext(conf)
    val employee = new ListBuffer[String]
    for(i <- 1 to 10){
      employee += "employee" + i
    }
    //val employee = List("employee1","employee2","employee3","employee4","employee5","employee6","employee7","employee8","employee9","employee10")
    val rdd: RDD[String] = sc.parallelize(employee)
    //测试map跟mapPartition区别 map是作用于每一个元素，mapPartitions是作用于分区 比如获取数据库连接用这个
    //myMap(rdd)
    //myMapPartitions(rdd)
    val rdd2 = sc.parallelize(1 to 10, 5) //结果 res2: Array[Int] = Array(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

    //把集合里的元素按照分区放到数组里返回 Array[Array[Int]]
    rdd2.glom().collect() //结果Array[Array[Int]] = Array(Array(1, 2), Array(3, 4), Array(5, 6), Array(7, 8), Array(9, 10))

    //过滤出符合条件的结果
    rdd2.filter(_ >2).collect() //结果 res4: Array[Int] = Array(3, 4, 5, 6, 7, 8, 9, 10)
    //两个过滤条件 先执行第一个过滤 完成后再执行第二个过滤
    rdd2.filter(x => x%2 == 0 && x > 6).collect() //结果 res5: Array[Int] = Array(8, 10)
    //取样sample


    //zip 拉链 把rdd里面对应的元素合并起来 前提条件：分区数相同和元素个数相同
    val rddZip1 = sc.parallelize(List("aaa", "bbb", "ccc"))
    val rddZip2 = sc.parallelize(List(30, 18, 60))

    rddZip1.zip(rddZip2).collect() //结果 res6: Array[(String, Int)] = Array((aaa,30), (bbb,18), (ccc,60))

    //给合并的元素加序号 res7: Array[((String, Int), Long)] = Array(((aaa,30),0), ((bbb,18),1), ((ccc,60),2))
    rddZip1.zip(rddZip2).zipWithIndex().collect()

    //交并补
    val rddleft = sc.parallelize(List(1, 2, 3,4,5,6),3)
    val rddright = sc.parallelize(List(1,2,3,4,5,6,7,8,8),2)

    //将两个rdd的元素合并起来 分区有5个  分区数等于合并前分区之和
    val unionRDD = rddleft.union(rddright)
    unionRDD.collect() //结果 res8: Array[Int] = Array(1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 7, 8, 8)

    //分区数
    unionRDD.partitions.size//分区数 结果 res10: Int = 5

    //交集
    rddleft.intersection(rddright).collect() //结果 res11: Array[Int] = Array(6, 3, 4, 1, 5, 2)

    rddleft.subtract(rddright).collect() //求出在左边并且不再右边的元素 结果为空 res16: Array[Int] = Array()

    rddleft.cartesian(rddright)//笛卡尔积 结果 res15: Array[(Int, Int)] = Array((1,1), (1,2), (1,3), (1,4), (2,1), (2,2), (2,3), (2,4), (1,5), (1,6), (1,7), (1,8), (1,8), (2,5), (2,6), (2,7), (2,8), (2,8), (3,1), (3,2), (3,3), (3,4), (4,1), (4,2), (4,3), (4,4), (3,5), (3,6), (3,7), (3,8), (3,8), (4,5), (4,6), (4,7), (4,8), (4,8), (5,1), (5,2), (5,3), (5,4), (6,1), (6,2), (6,3), (6,4), (5,5), (5,6), (5,7), (5,8), (5,8), (6,5), (6,6), (6,7), (6,8), (6,8))

    rddright.distinct().collect()//去重 去重前后分区数一样 结果 res17: Array[Int] = Array(4, 6, 8, 2, 1, 3, 7, 5)

    //去重 并重新设置分区
    rddright.distinct(4).partitions.size//去重，去重后的分区为 res18: Int = 4

    //查看分区编号以及分区的元素
    //结果 res19: Array[String] = Array(分区是: 0,元素是: 4, 分区是: 0,元素是: 8, 分区是: 1,元素是: 1, 分区是: 1,元素是: 5, 分区是: 2,元素是: 6, 分区是: 2,元素是: 2, 分区是: 3,元素是: 3, 分区是: 3,元素是: 7)
    rddright.distinct(4).mapPartitionsWithIndex((index,partition)=>{
      partition.map(x => s"分区是: $index" + s",元素是: $x")
    }).collect()

    //面试题 使用Spark Core RDD算子实现去重 不能使用distinct
    //思路：借助mapreduce去重 map输出(xx,nullWriterable) reduce 取出xx就可以了
    //查看distinct源码，就是下面这种实现方式
    rddright.map(x =>(x,null)).reduceByKey((x,y) => x).map((_._1)).collect()
    //每一步的返回值
    // res56: Array[(Int, Null)] = Array((1,null), (2,null), (3,null), (4,null), (5,null), (6,null), (7,null), (8,null), (8,null))
    //res58: Array[(Int, Null)] = Array((4,null), (6,null), (8,null), (2,null), (1,null), (3,null), (7,null), (5,null))
    //res59: Array[Int] = Array(4, 6, 8, 2, 1, 3, 7, 5)


    //scala拼接字符串用法
    val name = "JJ"
    println(s"hello $name")
    val user = User("aaa",18)
    println(s"hello ${user.name}")


    //coalesce合并小文件 应用于将分区大变小 把rdd的分区数减少到传进来的partitions数 减少分区数不会有shuffle，增大分区数会有shuffle操作
    //减小到2个分区 理解：将两个红包里的钱放到一个红包里，直接放进去就行；放到四个红包里，还需要把钱拆开，需要数据重新纷发
    val rdd3 = rdd2.coalesce(2)  //结果分区数为 res22: Int = 2

    //第二个参数 shuffle：true
    val rdd4 = rdd2.coalesce(6,true) //增到的6个分区，有shuffle操作  结果 res24: Int = 6

    //获取分区数
    rdd3.getNumPartitions

    //增加到6个分区，结果 res28: Int = 6
    // 看源码本质还是调的coalesce 一般应用于分区小变多
    val rdd5 = rdd2.repartition(6)

    val rddSort = sc.parallelize(List(("ruo",30),("j",18),("xing",60)))
    //sortBy和sortByKey会触发action
    //升序 默认升序
    rddSort.sortBy(_._2).collect()//结果 res29: Array[(String, Int)] = Array((j,18), (ruo,30), (xing,60))
    //降序 价格负号就是降序了
    rddSort.sortBy(-_._2).collect()//结果 res30: Array[(String, Int)] = Array((xing,60), (ruo,30), (j,18))

    //按照key排序，默认升序 思路：将kv对调 然后按照key排序 最后再对调回来 看源码
    rddSort.map(x=>(x._2,x._1)).sortByKey().map(x=>(x._2,x._1)).collect()
    rddSort.map(x=>(x._2,x._1)).sortByKey(false).map(x=>(x._2,x._1)).collect()//按照key排序，降序排列

    //遇到shuffle会切分stage
    //groupByKey()
    val rddGroup = sc.parallelize(List(("a",1),("b",2),("c",3),("a",99)))
    val groupByKeyRDD: RDD[(String, Iterable[Int])] = rddGroup.groupByKey()//分组
    rddGroup.groupByKey().collect() //结果 res31: Array[(String, Iterable[Int])] = Array((b,CompactBuffer(2)), (a,CompactBuffer(1, 99)), (c,CompactBuffer(3)))

    //mapValues() x.sum
    groupByKeyRDD.mapValues(x => x.sum).collect() //结果 res34: Array[(String, Int)] = Array((b,2), (a,100), (c,3))

    //reduceByKey() byKey的算子要做用到（k，v）类型的RDD上 里面有一个combine操作，相当于西安本地聚合在shuffle
    rddGroup.reduceByKey(_+_).collect() //结果 res35: Array[(String, Int)] = Array((b,2), (a,100), (c,3))

    val text = sc.textFile("file:///home/hadoop/data/wc.txt")
    val value: RDD[(String, Int)] = text.flatMap(_.split("\t")).map((_, 1))
    //按照value的值进行排序
    value.map(x =>(x._2,x._1)).sortByKey().map(x =>(x._2,x._1))//先将key和value对调，然后按照key排序，最后再将结果换回来

    /**
     * MapReduce:
     * map: 一行一行内容读取出来，按照分隔符拆分
     * 然后每个单词赋值上1
     * reduce：
     * 规约操作：每个单词出现的次数求和
     */
    //groupByKey()和reduceByKey()的区别 reduceByKey()在分区中有一次预聚合
    //在spark的ui上查看：groupByKey的shuffle的数据量明显高于reduceByKey
    //查看源码reduceByKey 点进去，一直点到PairRDDFunctions.scala类中的combineByKeyWithClassTag方法中，有一个参数mapSideCombine为true表示本地聚合

    text.flatMap(_.split("\t")).map((_, 1)).reduceByKey(_+_).collect()
    text.flatMap(_.split("\t")).map((_, 1)).groupByKey().map(x => (x._1,x._2.sum)).collect()

    //join
    val joinRDD1 = sc.parallelize(List(("ruo", "北京"), ("jj", "北京"), ("xing", "北京")))
    val joinRDD2 = sc.parallelize(List(("ruo","30"),("jj","18"),("xing","17")))

    //结果：res50: Array[(String, (String, String))] = Array((xing,(北京,17)), (jj,(北京,18)), (ruo,(北京,30)))
    joinRDD1.join(joinRDD2).collect()

    //左连接 右边可能没有值 所以是option；看leftjoinR的源码，底层就是调的cogroup

    //结果 res51: Array[(String, (String, Option[String]))] = Array((xing,(北京,Some(17))), (jj,(北京,Some(18))), (ruo,(北京,Some(30))))
    joinRDD1.leftOuterJoin(joinRDD2).collect()

    //结果 res52: Array[(String, (Option[String], String))] = Array((xing,(Some(北京),17)), (jj,(Some(北京),18)), (ruo,(Some(北京),30)))
    joinRDD1.rightOuterJoin(joinRDD2).collect()

    //结果：res53: Array[(String, (Option[String], Option[String]))] = Array((xing,(Some(北京),Some(17))), (jj,(Some(北京),Some(18))), (ruo,(Some(北京),Some(30))))
    joinRDD1.fullOuterJoin(joinRDD2).collect()

    //cogroup跟join的区别是cogroup的结果类型是可迭代的
    //结果：res54: Array[(String, (Iterable[String], Iterable[String]))] = Array((xing,(CompactBuffer(北京),CompactBuffer(17))), (jj,(CompactBuffer(北京),CompactBuffer(18))), (ruo,(CompactBuffer(北京),CompactBuffer(30))))
    joinRDD1.cogroup(joinRDD2).collect()

    sc.stop()
  }


  case class User(name:String,age:Int)
  def myMap(rdd:RDD[String]): Unit ={
    rdd.map(x =>{
      println("=====")
      x
    }).foreach(println)
  }
  def myMapPartitions(rdd:RDD[String]): Unit ={
    println("partition.size:"+rdd.getNumPartitions)

    rdd.mapPartitions(partition =>{
      println("=======")
      partition
    }).foreach(println)
  }

}

