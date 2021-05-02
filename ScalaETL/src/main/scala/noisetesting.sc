import org.apache.spark._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql._
import spark.implicits._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._


val read_path = "/user/jl11257/big_data_project/traces/processed/week_6_day_5_gps"
val write_path = c

// Bounds for how long an accident lasts (seconds)
val min_accident_duration = 420
val max_accident_duration = 720

//What percent of the dataset to drop
val drop_percent = 0.10


// Read in trace file (with no noise)
val trace_file_df = (spark.read.parquet(read_path)
                            .withColumn("edge", expr("substring(lane, 0, length(lane)-2)"))
                            .withColumn("to_node", split(col("edge"),"(?<!g)(to)(?!n)")(1))
                            .withColumn("day", (col("time") / (60*60*24)).cast(IntegerType))
                            .withColumn("time_of_day", col("time") % (60*60*24)))

// Which nodes accidents happen on (not on graph borders), what time accident happens,
// how long accident lasts
val node_table_path = "/user/jl11257/big_data_project/graph/node_table.csv"
// Sequence of potential accident nodes (all inner nodes)
val accident_candidates = (spark.read.format("csv")
                                .option("header", "true")
                                .load(node_table_path)
                                .withColumnRenamed("id", "to_node")
                                .withColumn("ave", split(col("to_node"),"_")(0))
                                .withColumn("st", split(col("to_node"),"_")(1))
                                .filter((col("ave") !== "9") && (col("ave") !== "Lexington") && (col("st") !== "30") && (col("st") !== "58"))
                                .drop("ave","st")
                                .collect
                                .toSeq)

val dummyArray = udf((d: Int) => (0 until d + 1).toArray)
spark.udf.register("dummyArray",dummyArray)

val rand_gen = scala.util.Random
val randfn = udf((r: Int) => rand_gen.nextInt(r))
spark.udf.register("randfn",randfn)

val days = trace_file_df.select("day").distinct
// val num_accidents = days.withColumn("accidentCount", explode(dummyArray(randfn(lit(3))))).filter(col("accidentCount") > 0)
// for testing
val num_accidents = days.withColumn("accidentCount", lit(2))

// modified this to simplify a little, basically every morning there is a blockage and SOME evenings
case class Accident(accident_start_time: Int, accident_end_time: Int, to_node: String)
val accident = udf((choice: Int) => {
		val windowlength = if (choice == 1) 3 else 5
		val startTime = if (choice == 2) 7 else 14
		val duration = rand_gen.nextInt(max_accident_duration-min_accident_duration)+min_accident_duration
        val start_time = rand_gen.nextInt(windowlength*60*60-duration+1)+(startTime*60*60)
        val end_time = start_time+duration
        val node = accident_candidates(rand_gen.nextInt(accident_candidates.length))(0).toString
        Accident(start_time, end_time, node)
    }
)

val accidents = num_accidents.withColumn("accident", accident(col("accidentCount"))).select("day","accident.*")
// so it doesn't re-simulate with each run in testing
accidents.cache

val affected_vehicles = (trace_file_df.join(accidents, Seq("day", "to_node"))
									 .filter((col("time_of_day")>=col("accident_start_time")) && 
                                      	(col("time_of_day")<=col("accident_end_time")))
									 .select("id","accident_start_time", "accident_end_time").distinct)
                                      

val traces_split = (trace_file_df.join(affected_vehicles, Seq("id"), "leftouter")
								 .withColumn("adjust", when(col("accident_start_time").isNotNull,
								 							col("time_of_day") >= col("accident_start_time")
								 						).otherwise(lit(false))))

val traces_to_leave = traces_split.filter(col("adjust") === false).select("time", "id", "x", "y", "angle", "type", "lane")
val traces_to_mod = traces_split.filter(col("adjust") === true)

traces_to_mod.cache

val repeatrec = (traces_to_mod.groupBy("id")
 					.agg(min("time_of_day"), first("accident_end_time", ignoreNulls=true))
 					.withColumnRenamed("min(time_of_day)","time_of_day")
 					.withColumnRenamed("first(accident_end_time, true)", "accident_end_time")
 					.withColumn("repeatCount", col("accident_end_time") - col("time_of_day")) // this is how many repeats we insert
 					.withColumn("repeatRow", lit(1)))

val rowchoice = (traces_to_mod.join(repeatrec.select("id", "time_of_day", "repeatRow"), Seq("id", "time_of_day"), "leftouter").na.fill(0)
 					 .join(repeatrec.select("id", "repeatCount"), Seq("id")))

val repeats = (rowchoice.filter(col("repeatRow")===1)
						.withColumn("dummy",explode(dummyArray(col("repeatCount")))) // generate repeated rows
						.withColumn("new_time", col("time") + col("dummy"))
						.drop("dummy"))
val regulars = rowchoice.filter(col("repeatRow")===0).withColumn("new_time", col("time") + col("repeatCount")) // push back times on other rows for vehicle

val modified_traces = repeats.union(regulars).select("new_time", "id", "x", "y", "angle", "type", "lane").withColumnRenamed("new_time","time")

val final_traces = traces_to_leave.union(modified_traces)

val dataloss = final_traces.sample(false,1.0-drop_percent)

dataloss.repartition(4).write.parquet(write_path)
