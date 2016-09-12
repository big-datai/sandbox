///**
//  * Created by dpavlov on 12/09/2016.
//  */
//
//import org.apache.spark.sql.SparkSession
//import org.scalatest.{FlatSpec, GivenWhenThen, Matchers}
//
//class TestSpark extends FlatSpec with GivenWhenThen with Matchers {
//
//  Given("spark")
//  val spark = SparkSession
//    .builder
//    .master("local[*]")
//    .appName("explore")
//    .getOrCreate()
//  val sc=spark.sparkContext
//
//  "Empty set" should "be counted" in {
//    Given("empty set")
//    val lines = Array("")
//
//    When("count words")
//    val wordCounts = WordCount.count(sc, sc.parallelize(lines)).collect()
//
//    Then("empty count")
//    wordCounts shouldBe empty
//  }
//
//  "Shakespeare most famous quote" should "be counted" in {
//    Given("quote")
//    val lines = Array("To be or not to be.", "That is the question.")
//
//    Given("stop words")
//    val stopWords = Set("the")
//
//    When("count words")
//    val wordCounts = WordCount.count(sc, sc.parallelize(lines), stopWords).collect()
//
//    Then("words counted")
//    wordCounts should equal(Array(
//      WordCount("be", 2),
//      WordCount("is", 1),
//      WordCount("not", 1),
//      WordCount("or", 1),
//      WordCount("question", 1),
//      WordCount("that", 1),
//      WordCount("to", 2)))
//  }
//
//}