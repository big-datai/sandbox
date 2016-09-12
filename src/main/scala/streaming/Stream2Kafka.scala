package streaming

/**
  * Created by dpavlov on 26/07/2016.
  */

import java.util.{Timer, TimerTask}

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.spark._
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming._


object Stream2Kafka {
  def main(args: Array[String]): Unit = {


    val conf = new SparkConf().setMaster("local-cluster[1,4,3200]")
    //local-cluster[numSlaves, coresPerSlave, memoryPerSlave]
    val sc = new SparkContext()
    val ssc = new StreamingContext(sc, Seconds(10))
    val topic = "output"
    val brokers = "hslave01.dwhpoc.cws.cyren.corp:6667,hslave02.dwhpoc.cws.cyren.corp:6667,hslave03.dwhpoc.cws.cyren.corp:6667"
    val rdd = sc.parallelize(1 until 10 toList, 5).map {
      _.toString
    }
    val timer = new Timer("pusher", true)
    timer.schedule(function2TimerTask(topic, brokers, rdd), 1000, 4000)

    Thread.sleep(1000 * 160)
    timer.cancel()

    ssc.start()
    ssc.awaitTerminationOrTimeout(100000)
    ssc.stop(false)
  }

  /**
    * Created by dpavlov on 24/07/2016.
    */

  implicit def function2TimerTask(topic: String, brokers: String, rdd: RDD[String]): TimerTask = {
    return new TimerTask {
      def run() = task(topic, brokers, rdd)
    }
  }

  def task(topic: String, brokers: String, rdd: RDD[String]): Unit = {

    rdd.foreachPartition(
      partitionOfRecords => {
        val props = new java.util.HashMap[String, Object]()
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers)
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
          "org.apache.kafka.common.serialization.StringSerializer")
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
          "org.apache.kafka.common.serialization.StringSerializer")
        val producer = new KafkaProducer[String, String](props)

        partitionOfRecords.foreach {
          case x: String => {
            println(x)
            val message = new ProducerRecord[String, String](topic, null, x)
            producer.send(message)
          }
        }
      })
  }
}