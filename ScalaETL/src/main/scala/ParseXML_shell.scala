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

val path = read_path + "week_0_day_0_trace.xml"
val df = sqlContext.read.format("com.databricks.spark.xml").option("rowTag", "timestep").load(path)
val df_new = df.filter(pmod($"_time", lit(60)) === 0).withColumn("data", $"vehicle".getItem(0)).select($"_time".as("time").cast("Int"),$"data._id".as("id"), $"data._x".as("x"), $"data._y".as("y"), $"data._angle".as("angle").cast("Int"), $"data._type".as("type"), $"data._speed".as("speed"), $"data._pos".as("pos"), $"data._lane".as("lane"), $"data._slope".as("slope").cast("Int"))   
df_new.write.parquet(write_path + "test1")
