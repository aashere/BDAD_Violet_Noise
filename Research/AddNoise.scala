import org.apache.spark._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql._

object AddNoise {
  def main(args: Array[String]) {

    val spark = SparkSession.builder().appName("AddNoise").getOrCreate
    import spark.implicits._

	val dirpath = "/user/jl11257/big_data_project/"
	val read_path = dirpath + args(0)
	val write_path = dirpath + args(1)

    // Bounds for how long an accident lasts (seconds)
    val min_accident_duration = 420
    val max_accident_duration = 720

    //What percent of the dataset to drop
    val drop_percent = args(2).toDouble

    //For random number generation
    val seed = 1
    val rand_gen = scala.util.Random(seed)

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
                                    .filter(col("ave") !== "9" && col("ave") !== "Lexington" && col("st") !== "30" && col("st") !== "58")
                                    .drop("ave","st")
                                    .collect
                                    .toSeq)
    // Array for accidents
    // Day, accident start time, accident end time, node where accident happens
    val accidents_arr = new Array[Row[Int,Int,Int,String]]()
    val day_agg = trace_file_df.agg(min("day"),max("day")).collect()(0)
    val first_day = day_agg(0)
    val last_day = day_agg(1)
    var i = 0
    for(i<-first_day to last_day){
        // Choose how many accidents this day will have (0, 1, or 2)
        val num_accidents = rand_gen.nextInt(3)

        // (For 1 accident only) Morning (0) or night (1) rush hour?
        val rush_hour = rand_gen.nextInt(2)
        if(num_accidents == 2 || (num_accidents == 1 && rush_hour == 0)){
            // Choose how long the accident will be
            val accident_duration = rand_gen.nextInt(max_accident_duration-min_accident_duration)+min_accident_duration
            // Choose the start and end time of accident such that the entire accident duration 
            // falls between 7-10AM
            val accident_start_time = rand_gen.nextInt(3*60*60-accident_duration+1)+(7*60*60)
            val accident_end_time = accident_start_time+accident_duration
            // Choose what node the accident will occur on
            val accident_node = accident_candidates(rand_gen.nextInt(accident_candidates.length))
            // Append to the accidents array
            accidents_arr :+ Row(i,accident_start_time,accident_end_time,accident_node)
        }
        if(num_accidents == 2 || (num_accidents == 1 && rush_hour == 1)){
            // Choose how long the accident will be
            val accident_duration = rand_gen.nextInt(max_accident_duration-min_accident_duration)+min_accident_duration
            // Choose the start and end time of accident such that the entire accident duration 
            // falls between 2-7PM
            val accident_start_time = rand_gen.nextInt(5*60*60-accident_duration+1)+(14*60*60)
            val accident_end_time = accident_start_time+accident_duration
            // Choose what node the accident will occur on
            val accident_node = accident_candidates(rand_gen.nextInt(accident_candidates.length))
            // Append to the accidents array
            accidents_arr :+ Row(i,accident_start_time,accident_end_time,accident_node)
        }
    }

    // Array of duplicates to union back to original dataset
    val duplicates_arr = new Array()
    // Loop through each accident
    for(i<-0 to accidents_arr.length-1){
        val accident = accidents_arr(i)
        // Get all vehicles that will be in an edge incoming to the node during accident duration
        val affected_vehicles = (trace_file_df.filter(col("day") === accident(0) && 
                                                        col("time_of_day")>=accident(1) && 
                                                        col("time_of_day")<=accident(2) && 
                                                        col("to_node") === accident(3)))
        // Get only the records for when vehicles enter an affected edge
        val vehicle_enter = (affected_vehicles.groupBy("id")
                                                .agg(min("time_of_day"))
                                                .withColumnRenamed("id","vehicle_id")
                                                .join(affected_vehicles, col("vehicle_id") === affected_vehicles.col("id") &&
                                                                            col("min(time_of_day)") === affected_vehicles.col("time_of_day"))
                                                .drop("vehicle_id","min(time_of_day)")
                                                .toSeq)
        // Duplicate these records accident_end_time - vehicle_entrance_time times
        // Loop through each vehicle record
        var j = 0
        for(j<-0 to vehicle_enter.length-1){
            val vehicle_record = vehicle_enter(j)
            // TODO: Need to check what the index of time_of_day is in vehicle_enter
            val idx = 12
            val num_duplicates = accident(2) - vehicle_record(idx)
            var k = 0
            for(k<-0 to num_duplicates-1){
                duplicates_arr :+ vehicle_record
            }
        }
    }

    // Parallelize and union back to original dataset
    val trace_file_with_duplicates = (spark.sparkContext.parallelize(duplicates_arr)
                                                        .union(trace_file_df)
                                                        .drop("edge","to_node","day","time_of_day"))

    //Drop data
    val noisy_trace_file = trace_file_with_duplicates.sample(false,1.0-drop_percent,seed)

    // Write out
    noisy_trace_file.coalesce(1).write.parquet(write_path)

    spark.stop()
  }
}
