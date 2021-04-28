import org.apache.spark._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.types._
import org.apache.spark.sql.Column


object VehicleFeatureGen {
    def main(args: Array[String]) {
        val spark = SparkSession.builder().appName("VehicleFeatureGen").getOrCreate
        import spark.implicits._

		val trace_path = args(0)
		val write_path = args(1)
		val nodes_path = "/user/jl11257/big_data_project/graph/nodes"
		val vertices_path = "/user/jl11257/big_data_project/graph/vertices"

		val node_df = spark.read.parquet(nodes_path).withColumnRenamed("vertex_id", "node_id")
		val edge_node_df = spark.read.parquet(vertices_path)
		val gps_df = spark.read.parquet(trace_path).withColumn("edge_id", expr("substring(lane, 0, length(lane)-2)")).repartition(col("id"))

		gps_df.cache()

		val starttms = gps_df.groupBy("id").agg(min("time")).withColumnRenamed("min(time)","time")
		val endtms = gps_df.groupBy("id").agg(max("time")).withColumnRenamed("max(time)","time")

		// start node
		val start_node_ft = (gps_df.join(starttms, Seq("id","time"), "inner")
						.join(edge_node_df.select("edge_id", "from_vertex_id"), Seq("edge_id"), "inner")
						.withColumnRenamed("from_vertex_id","start_vertex_id")
						.select("id","start_vertex_id"))

		// end node
		val stop_node_ft = (gps_df.join(endtms, Seq("id","time"), "inner")
						.join(edge_node_df.select("edge_id", "to_vertex_id"),  Seq("edge_id"), "inner")
						.withColumnRenamed("to_vertex_id","stop_vertex_id")
						.select("id","stop_vertex_id"))

		// calculating speed for trip
		val speed_fts = (gps_df.groupBy("id").agg(max("speed"), avg("speed"))
							.withColumnRenamed("max(speed)", "maxSpeed")
							.withColumnRenamed("avg(speed)", "averageSpeed"))

		// calculating the starting hour and minute
		val time_fts = (starttms.withColumn("hour", ($"time" % (24 * 60 * 60)) / (60 * 60)).withColumn("hour", col("hour").cast("int"))
						.withColumn("minute", (($"time" % (24 * 60 * 60)) % (60 * 60)) / 60).withColumn("minute", col("minute").cast("int")))


		// angle feature -- to calculate the turns the vehicle makes
		val windowSpec = Window.partitionBy("id").orderBy("time")
		val df_valid_angle = gps_df.filter(($"angle" === "0.00" ) || ($"angle" === "90.00" ) || ($"angle" === "180.00" ) || ($"angle" === "270.00" )).orderBy("id", "time")
		val df_angle_minus = (df_valid_angle.withColumn("angleLag", lag("angle", 1) over windowSpec)
								.withColumn("angleMinus", $"angle" - $"angleLag")
								.filter(!(isnull($"angleMinus")) && ($"angleMinus" !== 0)))
		val turncount_ft = df_angle_minus.groupBy("id").count().withColumnRenamed("count", "turnsCount")
		//.na.fill(0, Array("turnsCount")).drop("angle")

		val carlabel = (gps_df.groupBy("id").agg(first("type", ignoreNulls=true))
						.withColumnRenamed("first(type, true)", "type")
						.withColumn("type", when($"type" === "Bus", $"type").otherwise(lit("Car"))))

		val all_features = (carlabel.join(turncount_ft, Seq("id"),"left").na.fill(0, Array("turnsCount"))
									.join(time_fts, Seq("id"),"inner")
									.join(speed_fts, Seq("id"),"inner")
									.join(start_node_ft, Seq("id"),"inner")
									.join(stop_node_ft, Seq("id"),"inner"))

		all_features.repartition(12).write.mode("append").parquet(write_path)
	}
}

