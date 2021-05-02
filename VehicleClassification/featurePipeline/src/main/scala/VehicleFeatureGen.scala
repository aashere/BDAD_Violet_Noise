import org.apache.spark._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.types._
import org.apache.spark.sql.Column
import scala.math.pow
import scala.math.sqrt


object VehicleFeatureGen {
    def main(args: Array[String]) {
        val spark = SparkSession.builder().appName("VehicleFeatureGen").getOrCreate
        import spark.implicits._

		val trace_path = args(0)
		//val trace_path = "/user/jl11257/big_data_project/testing/noisedatatest2"
		val write_path = args(1)
		val nodes_path = "/user/jl11257/big_data_project/graph/nodes"
		val vertices_path = "/user/jl11257/big_data_project/graph/vertices"

		val node_df = spark.read.parquet(nodes_path).withColumnRenamed("vertex_id", "node_id")
		val edge_node_df = spark.read.parquet(vertices_path)
		val gps_df = (spark.read.parquet(trace_path)
							.withColumn("edge_id", expr("substring(lane, 0, length(lane)-2)"))
							.repartition(col("id")).orderBy("id", "time"))

		val windowSpec = Window.partitionBy("id").orderBy("time")
		val timeDistance = (gps_df.withColumn("timeDiff", lag("time", 1) over windowSpec)
								.withColumn("timeDiff", $"time" - $"timeDiff")
								.withColumn("xDiff", lag("x", 1) over windowSpec)
								.withColumn("xDiff", round($"x" - $"xDiff", 6))
								.withColumn("yDiff", lag("y", 1) over windowSpec)
								.withColumn("yDiff", round($"y" - $"yDiff", 6)))
			
		val speed = udf((xDiff: Double, yDiff: Double, time: Double) => { (sqrt(pow(xDiff,2) + pow(yDiff,2))) / time})
		spark.udf.register("speed", speed)

		val speed_df = (timeDistance.withColumn("speed", speed(col("xDiff"), col("yDiff"), col("timeDiff"))).na.fill(0.0)
							.drop("timeDiff").drop("xDiff").drop("yDiff"))

		val groupedfts = (speed_df.groupBy("id").agg(min("time"), max("time"), first("type", ignoreNulls=true),max("speed"), avg("speed"))
								.withColumnRenamed("min(time)","starttime")
								.withColumnRenamed("max(time)","stoptime")
								.withColumnRenamed("first(type, true)", "type")
								.withColumn("type", when($"type" === "Bus", $"type").otherwise(lit("Car")))
								.withColumnRenamed("max(speed)", "maxSpeed")
								.withColumn("averageSpeed",round(col("avg(speed)"),6))
								.drop("avg(speed)")
								.repartition(col("id")))
		groupedfts.cache()					

		val starttms = groupedfts.select("id","starttime").withColumnRenamed("starttime","time")
		// start node
		val start_node_ft = (gps_df.join(starttms, Seq("id","time"), "inner")
						.join(edge_node_df.select("edge_id", "from_vertex_id"), Seq("edge_id"), "inner")
						.withColumnRenamed("from_vertex_id","start_vertex_id")
						.select("id","start_vertex_id").repartition(col("id")))

		val endtms = groupedfts.select("id","stoptime").withColumnRenamed("stoptime","time")
		// end node
		val stop_node_ft = (gps_df.join(endtms, Seq("id","time"), "inner")
						.join(edge_node_df.select("edge_id", "to_vertex_id"),  Seq("edge_id"), "inner")
						.withColumnRenamed("to_vertex_id","stop_vertex_id")
						.select("id","stop_vertex_id").repartition(col("id")))

		// calculating the starting hour and minute
		val time_fts = (starttms.withColumn("hour", ($"time" % (24 * 60 * 60)) / (60 * 60))
								.withColumn("hour", col("hour").cast("int"))
								.withColumn("minute", (($"time" % (24 * 60 * 60)) % (60 * 60)) / 60)
								.withColumn("minute", col("minute").cast("int"))
								.drop("time"))


		// angle feature -- to calculate the turns the vehicle makes
		val df_valid_angle = gps_df.filter(($"angle" === "0.00" ) || ($"angle" === "90.00" ) || ($"angle" === "180.00" ) || ($"angle" === "270.00" )).orderBy("id", "time")
		val df_angle_minus = (df_valid_angle.withColumn("angleLag", lag("angle", 1) over windowSpec)
								.withColumn("angleMinus", $"angle" - $"angleLag")
								.filter(!(isnull($"angleMinus")) && ($"angleMinus" !== 0)))
		val turncount_ft = df_angle_minus.groupBy("id").count().withColumnRenamed("count", "turnsCount").repartition(col("id"))
		//.na.fill(0, Array("turnsCount")).drop("angle")

		val all_features = (groupedfts.join(time_fts, Seq("id"),"inner")
									.join(start_node_ft, Seq("id"),"inner")
									.join(stop_node_ft, Seq("id"),"inner")
									.join(turncount_ft, Seq("id"),"left").na.fill(0, Array("turnsCount"))
									.drop("starttime")
									.drop("stoptime"))

		all_features.repartition(6).write.mode("append").parquet(write_path)
	}
}

