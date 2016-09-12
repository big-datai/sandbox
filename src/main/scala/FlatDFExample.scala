import org.apache.spark.sql._

/**
  * Created by dpavlov on 27/07/2016.
  */
//spark-submit --jars $(echo /root/jars/*.jar | tr ' ' ',') --class ExploreHdfs2 --num-executors 3 --driver-memory 1g --executor-memory 1g --executor-cores 5 --master yarn --deploy-mode client /root/sandbox.jar

case class Clasif(category: String, hashed_value: String, open_value_length: Integer, open_value: String, as_class_id: Integer, as_class_level: Integer, as_enabled: Boolean, as_flags: Integer, as_source_id: Integer, vod_class_id: Integer, vod_threat_level: Integer, vod_enabled: Boolean, vod_flags: Integer, vod_source_id: Integer)

object ExploreHdfs2 {
  def main(args: Array[String]): Unit = {

    //creating Spark Session
    val spark = SparkSession
      .builder
      .master("local[*]")
      .appName("explore")
      .getOrCreate()
    //.setMaster("local-cluster[1,4,3200]")
    //local-cluster[numSlaves, coresPerSlave, memoryPerSlave]

    val rawTrans = spark.read.parquet("/tmp/out.parquet")
    import org.apache.spark.sql.functions._
    import spark.implicits._

    val flatClassifications = rawTrans.select($"transactionID", $"clientID", $"clasifications")
      .explode("clasifications", "clasifications1") { list: Seq[Clasif] => list.toList }
      .select("clientID", "clasifications1.*")
    val df_url = flatClassifications.where($"category" === "URL").groupBy("hashed_value").agg(countDistinct($"clientID")).orderBy(desc("count(clientID)"))
    val de_sender = flatClassifications.where($"category" === "SENDER_IP").groupBy("hashed_value").agg(countDistinct($"clientID")).orderBy(desc("count(clientID)"))

    df_url.write.parquet("/tmp/df_url")
    de_sender.write.parquet("/tmp/df_url")
  }
}
