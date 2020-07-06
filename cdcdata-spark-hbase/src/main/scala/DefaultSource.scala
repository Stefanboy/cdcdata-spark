import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.sources.{BaseRelation, RelationProvider}

/**
  * author：若泽数据-PK哥   
  * 交流群：545916944
  */
class DefaultSource extends RelationProvider{
  override def createRelation(sqlContext: SQLContext,
                              parameters: Map[String, String]): BaseRelation = {
    HBaseRelation(parameters)(sqlContext)
  }
}
