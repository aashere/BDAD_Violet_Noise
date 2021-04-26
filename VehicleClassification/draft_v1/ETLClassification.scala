import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{count, input_file_name, lit, mean, substring_index}
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.functions._
import org.apache.spark.sql._
import org.apache.spark.sql.types._
import org.apache.spark.sql.expressions.Window


object ETLClassification {
  def main(args: Array[String]) = {
    val spark = SparkSession.builder().appName("ETL - Vehicle Classification").master("local").getOrCreate
    import spark.implicits._

    val df_trace = spark.read.parquet("C:\\Users\\yingl\\OneDrive\\Desktop\\Data\\*")
      .withColumn("week", substring_index(substring_index(input_file_name(), "_", -4), "_", 1).cast(IntegerType))
      .withColumn("day", substring_index(substring_index(input_file_name(), "_", -2), "_", 1).cast(IntegerType))
      .withColumn("type", when($"type" === "Bus", $"type").otherwise(lit("Car")))
    df_trace.show(5)
    df_trace.printSchema()

    // angle feature -- to calculate the turns the vehicle makes by using count
    val windowSpec = Window.partitionBy($"week", $"day", $"id").orderBy('time)
    //val df_valid_angle = df_hour_min.filter(($"angle" === "0.00" ) || ($"angle" === "90.00" ) || ($"angle" === "180.00" ) || ($"angle" === "270.00" ))
    val df_angle_change = df_trace.withColumn("angle_lag", lag('angle, 1, 0) over windowSpec)
      .withColumn("angle_change", $"angle" - $"angle_lag")
    df_angle_change.show(10)


    val df_agg = df_angle_change.groupBy("week", "day", "id")
      .agg(
        first("type").alias("type"),
        first("lane").alias("start_lane"),
        last("lane").alias("end_lane"),
        max("speed").alias("max_speed"),
        count(lit(1)).alias("num_times"),
        sum(when($"angle_change" === 0,0).otherwise(1)).as("num_turns"),
        mean("speed").alias("avg_speed"))

    df_agg.show(10)
    df_agg.coalesce(1).write.format("csv").option("header", "true").save("C:\\Users\\yingl\\OneDrive\\Desktop\\Data_OLAP\\test1")

  }
}
