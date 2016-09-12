//import org.apache.spark.rdd.RDD
//import org.apache.spark.sql.SparkSession
//import org.apache.spark.streaming.{Seconds, StreamingContext}
//import org.scalatest.{BeforeAndAfter, FunSuite}
//
//import scala.collection.mutable
//
///**
//  * Created by dpavlov on 12/09/2016.
//  */
//case class WordCount(word: String, count: Int)
//
//object WordCount {
//  def count(lines: RDD[String], stopWords: Set[String]): RDD[WordCount] = {
//    val words = lines.flatMap(_.split("\\s"))
//      .map(_.strip(",").strip(".").toLowerCase)
//      .filter(!stopWords.contains(_)).filter(!_.isEmpty)
//
//    val wordCounts = words.map(word => (word, 1)).reduceByKey(_ + _).map {
//      case (word: String, count: Int) => WordCount(word, count)
//    }
//
//    val sortedWordCounts = wordCounts.sortBy(_.word)
//
//    sortedWordCounts
//  }
//}
//
//
//class StreamTest extends FunSuite with BeforeAndAfter {
//
//  private var spark: SparkSession = _
//  before {
//    spark = SparkSession
//      .builder
//      .master("local[*]")
//      .appName("explore")
//      .getOrCreate()
//    val lines = mutable.Queue[RDD[String]]()
//
//    val ssc = new StreamingContext(spark.sparkContext, Seconds(10))
//
//    val dstream = ssc.queueStream(lines)
//
//    // append data to DStream
//    lines += spark.sparkContext.makeRDD(Seq("To be or not to be.", "That is the question."))
//
//  }
//
//  after {
//
//
//  }
//
//}
