import org.apache.spark._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.types._
import org.apache.spark.sql.Column

val node_path = "/user/jl11257/big_data_project/graph/node_table.csv"
val edge_path = "/user/jl11257/big_data_project/graph/edge_table.csv"
val gps_path = "/user/jl11257/big_data_project/traces/processed/week_0_day_0_gps"

//Load node_table data into dataframe
val node_df = (spark.read.format("csv")
			.option("header", "true")
			.load(node_path)
			.withColumnRenamed("id", "node_id"))
// node_df.show

//Add indices as vertex_ids
val node_df_with_index = node_df.withColumn("vertex_id", monotonically_increasing_id)
// node_df_with_index.show

//Load edge_table data into dataframe
val edge_df = (spark.read.format("csv")
			.option("header","true")
			.load(edge_path)
			.withColumnRenamed("id", "edge_id"))
// edge_df.show

//Add from and to vertex id's
val edge_df_join_from = (edge_df.join(node_df_with_index
				.select(node_df_with_index("node_id"), node_df_with_index("vertex_id")), edge_df.col("from") === node_df_with_index.col("node_id"))
				.drop("node_id")
				.withColumnRenamed("vertex_id", "from_vertex_id"))
// edge_df_join_from.show

val edge_df_join_from_to = (edge_df_join_from.join(node_df_with_index
					.select(node_df_with_index("node_id"), node_df_with_index("vertex_id")), edge_df_join_from.col("to") === node_df_with_index.col("node_id"))
					.drop("node_id")
					.withColumnRenamed("vertex_id", "to_vertex_id"))
// edge_df_join_from_to.show

//Load gps table data with .parquet format into dataframe
val gps_df = spark.read.parquet(gps_path)
// gps_df.show

//Add start and stop columns
val gps_df_start_stop = (gps_df.withColumn("start", split(col("lane"),"(?<!g)(to)(?!n)")(0))
				.withColumn("stop", concat_ws("_", split(split(col("lane"),"(?<!g)(to)(?!n)")(1), "_")(0), split(split(col("lane"),"(?<!g)(to)(?!n)")(1), "_")(1))))
// gps_df_start_stop.show

//Add start_vertex_id and stop_vertex_id columns
val gps_df_join_start = (gps_df_start_stop.join(node_df_with_index
					.select(node_df_with_index("node_id"), node_df_with_index("vertex_id")), gps_df_start_stop.col("start") === node_df_with_index("node_id"))
					.drop("node_id")
					.withColumnRenamed("vertex_id", "start_vertex_id"))
// gps_df_join_start.show

val gps_df_join_start_stop = (gps_df_join_start.join(node_df_with_index
					.select(node_df_with_index("node_id"), node_df_with_index("vertex_id")), gps_df_join_start.col("stop") === node_df_with_index("node_id"))
					.drop("node_id")
					.withColumnRenamed("vertex_id", "stop_vertex_id"))
// gps_df_join_start_stop.show


// convert type Car1 and Car2 to type Car
val df_drop_columns_unused = gps_df_join_start_stop.drop("x", "y", "pos", "lane", "slope", "start", "stop")
// df_drop_columns_unused.show
val df_convert_car_type = df_drop_columns_unused.withColumn("type", when($"type" === "Bus", $"type").otherwise(lit("Car")))
// df_convert_car_type.show

// maxSpeed feature
val df_max_speed = df_convert_car_type.groupBy("id").agg(max("speed")).withColumnRenamed("max(speed)", "maxSpeed")
// df_max_speed.show
val df_max_speed_feature = df_convert_car_type.join(df_max_speed, Seq("id"), "left_outer")
// df_max_speed_feature.show

// averageSpeed feature
val df_average_speed = df_max_speed_feature.groupBy("id").agg(avg("speed")).withColumnRenamed("avg(speed)", "averageSpeed").withColumn("averageSpeed", round($"averageSpeed", 2))
// df_average_speed.show
val df_average_speed_feature = df_max_speed_feature.join(df_average_speed, Seq("id"), "left_outer")
// df_average_speed_feature.show

// convert time(second) to hour and minute
val df_hour = df_average_speed_feature.withColumn("hour", ($"time" % (24 * 60 * 60)) / (60 * 60)).withColumn("hour", col("hour").cast("int"))
// df_hour.show
val df_hour_min = df_hour.withColumn("minute", (($"time" % (24 * 60 * 60)) % (60 * 60)) / 60).withColumn("minute", col("minute").cast("int"))
// df_hour_min.show

// angle feature -- to calculate the turns the vehicle makes by using count
val windowSpec = Window.partitionBy('id).orderBy('time)
val df_valid_angle = df_hour_min.filter(($"angle" === "0.00" ) || ($"angle" === "90.00" ) || ($"angle" === "180.00" ) || ($"angle" === "270.00" ))
val df_angle_minus = df_valid_angle.withColumn("angleLag", lag('angle, 1) over windowSpec).withColumn("angleMinus", $"angle" - $"angleLag").orderBy("id", "time")
// df_angle_minus.show
val df_angle_minus_filter = df_angle_minus.filter(!(isnull($"angleMinus")) && ($"angleMinus" !== 0))
val df_count = df_angle_minus_filter.groupBy("id").count()
// df_count.show
val df_angle_feature = df_hour_min.join(df_count, Seq("id"), "left_outer").withColumnRenamed("count", "turnsCount").na.fill(0, Array("turnsCount")).drop("angle")
// df_angle_feature.show


// save as csv file
df_angle_feature.write.format("csv").option("header", "true").save("/user/jl11257/big_data_project/testing/vehicleClassification/processedDataOneDay")