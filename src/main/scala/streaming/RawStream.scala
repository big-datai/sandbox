package streaming

/**
  * Created by dpavlov on 22/08/2016.
  */

import java.text.SimpleDateFormat
import java.util.{Calendar, TimeZone}

import _root_.kafka.serializer.{DefaultDecoder, StringDecoder}
import org.apache.commons.lang.time.DateUtils
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka.KafkaUtils
import org.joda.time.DateTime

case class Data(key: String, v: Array[Byte])

object RawStream {
  def main(args: Array[String]): Unit = {

    //creating Spark Session
    val spark = SparkSession
      .builder
      .master("local[*]")
      .appName("RawStream")
      .getOrCreate()


    val ssc = new StreamingContext(spark.sparkContext, Seconds(10))
    val topicsSet = "ErezTest".split(",").toSet
    val kafkaParams = Map[String, String]("metadata.broker.list" -> "hslave01.dwhpoc.cws.cyren.corp:6667,hslave02.dwhpoc.cws.cyren.corp:6667,hslave03.dwhpoc.cws.cyren.corp:6667", "auto.offset.reset" -> "smallest")

    val input = KafkaUtils.createDirectStream[String, Array[Byte], StringDecoder, DefaultDecoder](ssc, kafkaParams, topicsSet)

    val sdf = new SimpleDateFormat("yyyy/MM/dd-hh:mm:ss")
    sdf.setTimeZone(TimeZone.getTimeZone("GMT"))

    input.foreachRDD { rdd =>

      if (!rdd.isEmpty()) {

        import spark.implicits._

        val events = rdd.map { event =>
          val eventStr = new String(event._2)
          val timeStamp = eventStr.substring(eventStr.lastIndexOf(",\"time\":") + 8, eventStr.length - 1).toLong * 1000
          val date = DateUtils.round(sdf.parse(new DateTime(timeStamp).toDateTime.toString("yyyy/MM/dd-hh:mm:ss")), Calendar.HOUR)
          val cal = Calendar.getInstance()
          cal.setTime(date)
          val bucket = "" + cal.get(Calendar.YEAR) + "/" +
            cal.get(Calendar.YEAR) + cal.get(Calendar.MONTH) + "/" +
            cal.get(Calendar.YEAR) + cal.get(Calendar.MONTH) + cal.get(Calendar.DAY_OF_MONTH) + "/" +
            cal.get(Calendar.YEAR) + cal.get(Calendar.MONTH) + cal.get(Calendar.DAY_OF_MONTH) + cal.get(Calendar.HOUR)

          Data(bucket, event._2)
        }.toDF().as[Data]

        val buckets = events.select($"key".as[String]).distinct.collect()

        buckets.foreach({ bucket =>
          val data2 = events.filter(_.key == bucket)
          println("++++++++++++++" + bucket + "+++++++++++++++")
          data2.write.format("parquet").save("/user/root/esp/raw/" + bucket)
        })
      }

    }
    ssc.start()
    ssc.awaitTermination()

  }
}
