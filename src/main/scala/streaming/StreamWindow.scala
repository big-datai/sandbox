package streaming

/**
  * Created by dpavlov on 24/07/2016.
  */

import _root_.kafka.serializer.{DefaultDecoder, StringDecoder}
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._

object StreamWindow {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .master("local[*]")
      .appName("window")
      .getOrCreate()
    val WINDOW_LENGTH = Seconds(60)
    val SLIDE_INTERVAL = Seconds(5)
    val ssc = new StreamingContext(spark.sparkContext, SLIDE_INTERVAL)
    val topicsSet = "output".split(",").toSet
    val kafkaParams = Map[String, String]("metadata.broker.list" -> "local:6667,local:6667,local:6667", "auto.offset.reset" -> "smallest")
    val input = KafkaUtils.createDirectStream[String, Array[Byte], StringDecoder, DefaultDecoder](ssc, kafkaParams, topicsSet)
    val inputWindow = input.window(WINDOW_LENGTH, SLIDE_INTERVAL)

    inputWindow.foreachRDD(rdd =>
      print(rdd.count() + "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
    )

    ssc.start()
    ssc.awaitTerminationOrTimeout(1000000)
    ssc.stop(false)
  }
}