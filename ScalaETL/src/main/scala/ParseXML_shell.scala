//spark-shell --deploy-mode client --packages com.databricks:spark-xml_2.10:0.4.1,com.databricks:spark-csv_2.10:1.5.0
//spark-shell --deploy-mode client --packages com.databricks:spark-xml_2.10:0.4.1,com.databricks:spark-csv_2.10:1.5.0 --num-executors 16 --driver-memory 10G --executor-memory 5G

import scala.xml._
import org.apache.spark.sql.SparkSession
import com.databricks.spark.xml._
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql._
import com.databricks.spark
import com.databricks.spark.xml

val sqlContext = new org.apache.spark.sql.SQLContext(sc)
val spark = SparkSession.builder().appName("testings").master("local").getOrCreate
import spark.implicits._
val read_path = "/user/jl11257/big_data_project/SimulationData_1/traces/"
val write_path = "/user/jl11257/big_data_project/sampleSchema/"


val path = read_path + "week_1_sumotrace.xml"
val df = sqlContext.read.format("com.databricks.spark.xml").option("rowTag", "timestep").load(path)
val flat = df.select($"_time", explode($"vehicle")).select($"_time",$"col.*")
  .select($"_time".as("time").cast("Int"),$"_id".as("id"), $"_x".as("x"), $"_y".as("y"),
	$"_angle".as("angle").cast("Int"), $"_type".as("type"), $"_speed".as("speed"),
	$"_pos".as("pos"), $"_lane".as("lane"), $"_slope".as("slope").cast("Int")).coalesce(6)
flat.write.parquet(write_path + "test1")
	