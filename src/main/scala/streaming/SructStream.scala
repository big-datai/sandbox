package streaming

/**
  * Created by dpavlov on 22/08/2016.
  */

import org.apache.spark.sql.SparkSession


object SructStream {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder
      .appName("StructuredNetworkWordCount")
        .master("local[2]")
      .getOrCreate()


    val inputDF = spark.readStream.text("file")

      inputDF.writeStream.format("parquet").start("/Users/dpavlov/data")

  }
}
