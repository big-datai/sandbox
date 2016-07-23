import org.apache.spark.sql.DataFrame
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by dmitry on 23/07/2016.
  */
class HelloTest {

  import org.apache.spark
  import org.apache.spark.sql.SQLContext

  def main(args: Array[String]): Unit = {
    println("+++++++++++++++++++++++++++++++++++++++++hello world")
  }

  val conf=new SparkConf().setAppName("SparkMain")
  val sc=new SparkContext(conf)
  val sqlContext: SQLContext = new SQLContext(sc)

  val d:DataFrame = sqlContext.read.parquet("s3n://AKIAJ5T3KGPTL2377SJQ:DOjTF2MC6jvHjXn4a9CsA15KfUpHhWubR+xNv7Xm@z-aegis-data-stage-5127cfa2b3f74e93a1f9baa30d72a7f5/compacted/*/*")
  val parquetFile =d.repartition(100)

  parquetFile.registerTempTable("compacted")
  parquetFile.select("ftp_data.*").filter(!(parquetFile("ftp_data.user")===null)).show()
  parquetFile.select("ftp_data.*").sort($"ftp_data.user".desc).show
  parquetFile.distinct()


 // select  ftp_data.user from compacted order by ftp_data.user des
}
