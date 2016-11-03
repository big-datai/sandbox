package streaming

/**
  * Created by dpavlov on 26/07/2016.
  */

import java.util.{Timer, TimerTask}

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.spark._
import org.apache.spark.rdd.RDD
import _root_.kafka.serializer.{DefaultDecoder, StringDecoder}



object Stream2Kafka {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[*]").setAppName("stream raw data")
    //local-cluster[numSlaves, coresPerSlave, memoryPerSlave]
    val sc = new SparkContext(conf)

    import org.apache.hadoop.conf.Configuration
    import org.apache.hadoop.mapreduce.Job
    import org.apache.hadoop.io.{LongWritable, Text}
    import org.apache.hadoop.mapreduce.lib.input.TextInputFormat

//    val config = new Configuration(sc.hadoopConfiguration)
//    config.set("textinputformat.record.delimiter", "F:")
//    val input = sc.newAPIHadoopFile("/Users/dpavlov/Desktop/c9rlog01.20161006/*/", classOf[TextInputFormat], classOf[LongWritable], classOf[Text], config).map(l=>l._2.toString)
//    val h3_data=input.filter(l=>l.take(20).contains("H:3"))
//    case class Mess(m:String,time:String,hostname:String)
//    val h3json=h3_data.map{l=>
//      val hex=l.split(",")(2).split("\\.")(1)
//      var ip= ""
//      for (j <-0 to hex.length()-1) {
//        if(j%2==0){
//          var sub = hex.substring(j, j+2)
//          var num = Integer.parseInt(sub, 16)
//          ip += num+"."
//          // println(num)
//        }
//      }
//      val h=ip.substring(0, ip.length()-1)//convertHexToIP(elem).toString
//      "{\"message\":"+"\""+l.replaceAll("\\n","")+"\","+ "\"time\":"+"\""+System.currentTimeMillis()+"\","+"\"hostname\":"+"\""+h+"\"}"
//      //Mess(l.replaceAll("\\n",""),System.currentTimeMillis().toString,h)
//    }
//    h3json.take(10).foreach(println)
    import org.apache.hadoop.io.compress.GzipCodec
    //h3json.coalesce(1000).saveAsTextFile("/Users/dpavlov/Desktop/h3json/", classOf[GzipCodec])


    val topic = "shuals"
    val brokers = "hslave01.dwhpoc.cws.cyren.corp:6667,hslave02.dwhpoc.cws.cyren.corp:6667,hslave03.dwhpoc.cws.cyren.corp:6667"


//    val rdd = sc.parallelize(1 until 10 toList, 5).map {
//      _.toString
//    }

    //val sample=sc.textFile("/Users/dpavlov/Desktop/h3json").takeSample(true,100,1234)
    //val rdd=sc.parallelize(sample)
    //val timer = new Timer("pusher", true)
    //timer.schedule(function2TimerTask(topic, brokers, rdd), 1000, 4000)
    val h3json=sc.textFile("/Users/dpavlov/Desktop/h3json")
    task(topic, brokers, h3json)
    //Thread.sleep(1000 * 160)
    //timer.cancel()

  }

  /**
    * Created by dpavlov on 24/07/2016.
    */


  def task(topic: String, brokers: String, rdd: RDD[String]): Unit = {

    rdd.coalesce(10).foreachPartition(
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