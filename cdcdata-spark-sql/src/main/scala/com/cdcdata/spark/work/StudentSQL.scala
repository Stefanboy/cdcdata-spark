package com.cdcdata.spark.work

import com.cdcdata.spark.work.StageETL.etlJson
import org.apache.spark.sql.SparkSession

/**
 * id  学号   课程    成绩
 * 1	1	chinese	43
 * 2	1	math	55
 * 3	2	chinese	77
 * 4	2	match	88
 * 5	3	chinese	98
 * 6	3	match	65
 *
 * 需求：
 * 所有数学课程成绩 大于 语文课程成绩的学生的学号
 */
object StudentSQL {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .master("local")
      .appName(this.getClass.getSimpleName)
      .getOrCreate()
    import spark.implicits._
    val df = spark.read.textFile("cdcdata-spark-sql/data/student.txt").map(x => {
      val split = x.split("\t")
      val id = split(0).trim.toLong
      val sid = split(1).trim.toLong
      val course = split(2).trim
      val result = split(3).trim.toLong
      (id, sid, course, result)
    }).toDF("id", "sid", "course", "result")
    //df.selectExpr()
    df.createOrReplaceTempView("student")

    /*spark.sql("select * from student s1 left join student s2 on s1.sid=s2.sid ")
        .show()*/
    spark.sql("select s1.sid from student s1 left join student s2 on s1.sid=s2.sid and s1.course='math' or (s1.sid=s2.sid and s2.course='chinese') where s1.result > s2.result")
      .show()
    spark.stop()
  }






}
