import org.apache.spark._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object ETLfromCSV {
  def main(args: Array[String]) = {
    // set up environment
    System.setProperty("hadoop.home.dir", "C:\\winutils")
    val conf = new SparkConf().setAppName("ETL from csv").setMaster("local")
    val sc = new SparkContext(conf)
    val spark = SparkSession.builder().appName("testings").master("local").getOrCreate

    // Extract & Transform
    val read_path = "C:\\Users\\yingl\\OneDrive\\Desktop\\BDAD_Proj\\microsimulation\\DataSchema\\sampleSchema\\" //"/user/jl11257/big_data_project/sampleSchema/node_table.csv"
    val write_path = "C:\\Users\\yingl\\OneDrive\\Desktop\\BDAD_Proj\\microsimulation\\DataSchema\\OLAP\\"

    // edge table & road table
    val edge_path = read_path + "edge_table.csv"
    val edge_df = spark.read.format("csv").option("header", "true").load(edge_path).withColumnRenamed("id", "edge_id")

    val road_path = read_path + "road_type.csv"
    val road_df = spark.read.format("csv").option("header", "true").load(road_path).withColumnRenamed("id", "road_id")
      .withColumnRenamed("speed", "speed_limit")

    val edge_road_df = edge_df.join(road_df, edge_df.col("type") === road_df.col("road_id")).drop("road_id")

    // gps table
    val gps_path = read_path + "gps_table.csv"
    val gps_raw_df = spark.read.format("csv").option("header", "true").load(gps_path).withColumnRenamed("id", "vehicle_id")
      .withColumnRenamed("type", "vehicle_type")
    val gps_df = gps_raw_df.withColumn("lane_id", substring_index(col("lane"), "_", -1))
      .withColumn("edge", substring_index(col("lane"), "_", 3)).drop("lane")
      .withColumn("date", lit(1))

    val gps_edge_df = gps_df.join(edge_road_df, gps_df.col("edge") === edge_road_df.col("edge_id")).drop("edge_id")

    // gps detail table
    val vehicle_path = read_path + "vehicle_type.csv"
    val vehicle_df = spark.read.format("csv").option("header", "true").load(vehicle_path).withColumnRenamed("id", "vehicle_id")
    val gps_detail_df = gps_edge_df.join(vehicle_df, gps_edge_df.col("vehicle_type") === vehicle_df.col("vehicle_id"))
      .drop("vehicle_id")
    //gps_detail_df.show

    // gps aggregation table
    val gps_agg_df = gps_detail_df.groupBy("date", "time", "edge")
      .agg(mean("speed").alias("avg_speed"),
        count(lit(1)).alias("num_vehicles")).sort("time")
    gps_agg_df.show

    // Load
    // gps_detail_df.write.format("csv").option("header", "true").save(write_path + "gps_detail")
    // gps_agg_df.write.format("csv").option("header", "true").save(write_path + "gps_agg")
  }
}
