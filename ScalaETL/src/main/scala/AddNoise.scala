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
    // Percent of nodes we want to create noise on
    val percent_noise = args(2).toDouble
    // How long an accident lasts (seconds)
    val accident_duration = 600
    val seed = 1

    // Read in trace file (with no noise)
    val trace_file_df = (spark.read.parquet(read_path)
                                .withColumn("edge", expr("substring(lane, 0, length(lane)-2)"))
                                .withColumn("to_node", split(col("edge"),"(?<!g)(to)(?!n)")(1)))
    val max_time = trace_file_df.agg(max("time")).collect()(0)(0)
    val rand_gen = scala.util.Random(seed)

    // Read in node table and grab percent_noise nodes randomly, add random timestamp column
    val node_table_path = "/user/jl11257/big_data_project/graph/node_table.csv"
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


    spark.stop()
  }
}
