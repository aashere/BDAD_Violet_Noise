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
    val last_day = trace_file_df.agg(max("day")).collect()(0)(0)
    // Day, accident start time, accident end time, node where accident happens
    val accidents_arr = new Array[Row[Int,Int,Int,String]]()
    var i = 0
    for(i<-0 to last_day){
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
    
    /*
    
    // Read in node table and grab percent_noise nodes randomly, add random timestamp column
    
    val noise_nodes = (spark.read.format("csv")
			.option("header", "true")
			.load(node_table_path)
			.withColumnRenamed("id", "to_node")
            .select("to_node")
            .sample(false,percent_noise,seed)
            .withColumn("time", rand_gen.nextInt(max_time+1)))

    // Get vehicle records on edges with the noisy nodes incoming at their noisy timestamps
    val noise_records = (trace_file_df.join(noise_nodes, Seq("to_node","time"),"inner")
                                        .withColumnRenamed("time","noise_start_time")
                                        .withColumn("noise_end_time",col("noise_start_time")+rand_gen.nextInt(max_noise_duration-min_noise_duration)+min_noise_duration)
                                        .select("id","noise_start_time","noise_end_time"))

    // Label the current no noise records and drop the real ones so we can replace with noisy ones
    val label = udf((time, start, end) => 
                                            if(start.isNotNull && end.isNotNull && time==start){
                                                "duplicate"
                                            }
                                            else if(start.isNotNull && end.isNotNull && time>start && time<=end){
                                                "drop"
                                            }
                                            else{
                                                "keep"
                                            })
    spark.udf.register("label",label)
    val keep_records = (trace_file_df.join(noise_records,Seq("id"),"leftouter")
                                        .withColumn("label", label(col("time"),col("noise_start_time"),col("noise_end_time")))
                                        .filter(col("label") !== "drop"))
    
    //Duplicate the noise records over their range
    */

    //Drop data
    //.sample(false,drop_percent,seed)

    spark.stop()
  }
}
