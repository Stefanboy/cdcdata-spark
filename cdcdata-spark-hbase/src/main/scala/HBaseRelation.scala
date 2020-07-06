import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.sql.sources.{BaseRelation, TableScan}
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}

import scala.collection.mutable.ArrayBuffer

/**
  * author：若泽数据-PK哥   
  * 交流群：545916944
  */
case class HBaseRelation(@transient val parameters: Map[String, String])
                        (@transient val sqlContext: SQLContext)
  extends BaseRelation with TableScan {

  val hbaseTable = parameters.getOrElse("hbase.table.name", sys.error("hbase.table.name is required..."))
  val sparkTableSchema = parameters.getOrElse("spark.table.schema", sys.error("spark.table.schema is required..."))
  private val sparkFields = HBaseDataSourceUtils.extractSparkFields(sparkTableSchema)

  override def schema: StructType = {
    val rows = sparkFields.map(field => {
      val structField = field.fieldType.toLowerCase match {
        case "int" => StructField(field.fieldName, IntegerType)
        case "string" => StructField(field.fieldName, StringType)
      }
      structField
    })
    new StructType(rows)
  }

  /**
    * 把HBase中的数据转成RDD[Row]
    * 1）怎么样去读取HBase的数据
    * 2）怎么样把HBase的转成RDD[Row]
    */
  override def buildScan(): RDD[Row] = {
    val hbaseConf = HBaseConfiguration.create()
    hbaseConf.set("hbase.zookeeper.quorum", "ruozedata001:2181")
    hbaseConf.set(TableInputFormat.INPUT_TABLE, hbaseTable)
    // TODO... hbaseConf.set(TableInputFormat.SCAN_COLUMNS,)

    val hbaseRDD: RDD[(ImmutableBytesWritable, Result)] = sqlContext.sparkContext.newAPIHadoopRDD(hbaseConf, //
      classOf[TableInputFormat], //
      classOf[ImmutableBytesWritable], //
      classOf[Result])

    // TODO... Result==>RDD[Row]

    hbaseRDD.map(_._2).map(result => {
      val buffer = new ArrayBuffer[Any]()
      sparkFields.foreach(field => {
        field.fieldType.toLowerCase match {
          case "string" => {
            val tmp = result.getValue(Bytes.toBytes("o"), Bytes.toBytes(field.fieldName))
            buffer += new String(tmp)
          }
          case "int" => {
            val tmp = result.getValue(Bytes.toBytes("o"), Bytes.toBytes(field.fieldName))
            buffer += Integer.valueOf(new String(tmp))
          }
        }
      })
      Row.fromSeq(buffer)
    })
  }
}
