import org.apache.spark._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.functions._

import scala.xml._
import org.apache.spark.sql.SparkSession
import com.databricks.spark.xml._
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql._
import com.databricks.spark
import com.databricks.spark.xml
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.types.{DoubleType, IntegerType, StringType, StructField, StructType}


object ParseXML {
  def main(args: Array[String]) {

    // set up environment
    System.setProperty("hadoop.home.dir", "C:\\winutils")
    val conf = new SparkConf().setAppName("ETL from csv").setMaster("local")
    val sc = new SparkContext(conf)
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
    val spark = SparkSession.builder().appName("testings").master("local").getOrCreate
    import spark.implicits._
    val read_path = "C:\\Users\\yingl\\OneDrive\\Desktop\\BDAD_Proj\\microsimulation\\sumoDataGeneration\\data\\"
    val write_path = "C:\\Users\\yingl\\OneDrive\\Desktop\\BDAD_Proj\\microsimulation\\DataSchema\\OLAP\\trace\\"

//    val customSchema = StructType(Array(
//      StructField("time", IntegerType, nullable = true),
//      StructField("id", StringType, nullable = true),
//      StructField("x", DoubleType, nullable = true),
//      StructField("y", DoubleType, nullable = true),
//      StructField("angle", IntegerType, nullable = true),
//      StructField("type", StringType, nullable = true),
//      StructField("speed", DoubleType, nullable = true),
//      StructField("pos", DoubleType, nullable = true),
//      StructField("lane", StringType, nullable = true),
//      StructField("slope", IntegerType, nullable = true)))

    val path = read_path + "week_1_sumotrace.xml"
    val df = sqlContext.read.format("com.databricks.spark.xml").option("rowTag", "timestep").load(path)
    val flat = df.select($"_time", explode($"vehicle")).select($"_time",$"col.*")
      .select($"_time".as("time").cast("Int"),$"_id".as("id"), $"_x".as("x"), $"_y".as("y"),
        $"_angle".as("angle").cast("Int"), $"_type".as("type"), $"_speed".as("speed"),
        $"_pos".as("pos"), $"_lane".as("lane"), $"_slope".as("slope").cast("Int"))
    flat.show()

//    val df_new = df.filter(pmod($"_time", lit(60)) === 0).withColumn("data", $"vehicle".getItem(0))
//      .select($"_time".as("time").cast("Int"),$"data._id".as("id"), $"data._x".as("x"), $"data._y".as("y"),
//        $"data._angle".as("angle").cast("Int"), $"data._type".as("type"), $"data._speed".as("speed"),
//        $"data._pos".as("pos"), $"data._lane".as("lane"), $"data._slope".as("slope").cast("Int"))
    flat.write.parquet(write_path + "test1")
  }
}
