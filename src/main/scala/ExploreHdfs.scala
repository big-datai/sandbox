
import java.sql.Date
import java.text.SimpleDateFormat

import org.apache.spark.sql.{Row, SparkSession}

/**
  * Created by dpavlov on 27/07/2016.
  */
object ExploreHdfs {

  def main(args: Array[String]): Unit = {


    val spark = SparkSession
      .builder
      .master("local[*]")
      .appName("explore")
      .getOrCreate()
    //.master("local-cluster[1,4,3200]")
    //local-cluster[numSlaves, coresPerSlave, memoryPerSlave]

    val parquetFile = spark.read.parquet("hdfs://hslave01.dwhpoc.cws.cyren.corp:8020/tmp/out.parquet")
    //hdfs://localhost:8020/tmp/people.txt
    //transforms timestamp to Date

    val simpleDateFormat = new SimpleDateFormat("yyyy-dd-mm")

    import org.apache.spark.sql.functions._
    import spark.implicits._
    val form = udf { (tmstmp: String) =>
      simpleDateFormat.format(new Date(tmstmp.toLong * 1000))
    }
    val df = parquetFile.withColumn("day", form($"timestamp"))
    df.createOrReplaceTempView("transactions")
    df.agg($"day").select($"day").show
    //    case class Line(categotyID: String, hash: String, length: String, openValue: String,
    //                    spamClassID: Integer, classifiLevel: Integereger, spamInd: Boolean, spamFlag: Integer, spamSourceID: Integer, vodID: Integer, vodVTL: Integer, vodInd: Boolean, vodFlag: Integer, vodSourceID:Integer)

    //case class Clasif(category:String,hashed_value:String,open_value_length:Integer,open_value:String,as_class_id:Integer,as_class_level:Integer,as_enabled:Boolean,as_flags:Integer,as_source_id:Integer,vod_class_id:Integer,vod_threat_level:Integer,vod_enabled:Boolean,vod_flags:Integer,vod_source_id:Integer)

    val arr = udf { (data: Seq[Clasif]) =>
      data
      //        .apply(0) match{
      //        case Clasif(category:String,hashed_value:String,open_value_length:Integer,open_value:String,as_class_id:Integer,as_class_level:Integer,as_enabled:Boolean,as_flags:Integer,as_source_id:Integer,vod_class_id:Integer,vod_threat_level:Integer,vod_enabled:Boolean,vod_flags:Integer,vod_source_id:Integer) =>
      //           Clasif(category, hashed_value,open_value_length, open_value,as_class_id,as_class_level,as_enabled,as_flags,as_source_id,vod_class_id,vod_threat_level,vod_enabled,vod_flags,vod_source_id)
      //      }
    }
    val tableOfClass = parquetFile.withColumn("class", arr($"clasifications")).select($"class")

    // val temp=parquetFile.select($"clientID",$"clasifications").explode("clasifications","clasifications1"){list:Seq[Clasif]=>list.toList}.
    //   select("clientID","clasifications1.*")

    //temp.select($"category",$"count(category)").groupBy("category")


    //val tableOfClass=parquetFile.select($"clasifications".getItem(1))
    // val tableOfClass=parquetFile.select($"clasifications".getField("category"))
    import scala.collection.mutable.ArrayBuffer
    parquetFile.select($"clasifications").explode("clasifications", "clasifications1") { list: ArrayBuffer[String] => list.toList }.show
    //val flat=tableOfClass.map(r=>r.asInstanceOf[Seq[Clasif]].flatten[Clasif])

    parquetFile.select($"clasifications").explode("clasifications", "clasifications1") { list: Seq[Clasif] => list.toList }.select("clasifications1").
      explode("clasifications1", "clasifications1new") { l: Clasif => ",,,,".split(",") }.show
    parquetFile.select($"clasifications")

    val temp = parquetFile.select($"clientID", $"clasifications").explode("clasifications", "clasifications1") { list: Seq[Clasif] => list.toList }.select("clientID", "clasifications1.*").show

    parquetFile.select($"clasifications").explode("clasifications", "clasifications1") { list: Seq[Clasif] => list.toList }.select("clasifications1").map {
      case Row(category: String, hashed_value: String, open_value_length: Integer, open_value: String, as_class_id: Integer, as_class_level: Integer, as_enabled: Boolean, as_flags: Integer, as_source_id: Integer, vod_class_id: Integer, vod_threat_level: Integer, vod_enabled: Boolean, vod_flags: Integer, vod_source_id: Integer) =>
        Clasif(category, hashed_value, open_value_length, open_value, as_class_id, as_class_level, as_enabled, as_flags, as_source_id, vod_class_id, vod_threat_level, vod_enabled, vod_flags, vod_source_id)
    }

  }

  case class Clasif(category: String, hashed_value: String, open_value_length: Integer, open_value: String, as_class_id: Integer, as_class_level: Integer, as_enabled: Boolean, as_flags: Integer, as_source_id: Integer, vod_class_id: Integer, vod_threat_level: Integer, vod_enabled: Boolean, vod_flags: Integer, vod_source_id: Integer)
}
