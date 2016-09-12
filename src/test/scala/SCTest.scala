import org.apache.spark.sql.SparkSession
import org.scalatest.{BeforeAndAfter, FunSuite}

/**
  * Created by dpavlov on 12/09/2016.
  */

class SCTest extends FunSuite with BeforeAndAfter {

  private var spark: SparkSession = _
  before {
    spark = SparkSession
      .builder
      .master("local[*]")
      .appName("explore")
      .getOrCreate()
  }
  after {
    if (spark.sparkContext != null) {
      spark.stop
    }

  }


}

