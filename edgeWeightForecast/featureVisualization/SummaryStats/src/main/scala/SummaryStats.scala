import org.apache.spark._
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.Column
import com.databricks.spark._
import org.apache.spark.sql.expressions.Window


object SummaryStats {
    def main(args: Array[String]) {
        val spark = SparkSession.builder().appName("SummaryStats").getOrCreate
        import spark.implicits._

        val read_dirpath = "/user/jl11257/big_data_project/"
        val write_dirpath = "/user/jl11257/big_data_project/visualizations/" 

        val delta_df_path = read_dirpath + args(0)
        val trace_file_path = read_dirpath + args(1)

        val histogram_overall_write_path = write_dirpath + "histogram/overall"
        val histogram_edge_avg_write_path = write_dirpath + "histogram/edge_avg"
        val histogram_interval_avg_write_path = write_dirpath + "histogram/interval_avg"

        val edge_time_series_write_path = write_dirpath + "edge_time_series"
        val ave_time_series_write_path = write_dirpath + "road_time_series/ave"
        val st_time_series_write_path = write_dirpath + "road_time_series/st"

        val total_trip_time_write_path = write_dirpath + "total_trip_time"

        //Read in delta feature table
        val delta_df = spark.read.parquet(delta_df_path).cache

        //HISTOGRAMS
        //Overall histogram (no aggregation)
        val histogram_overall = delta_df.select("interval","edge","t_0_density","t-1_delta","t-2_delta","t-3_delta","tot_vehicle_count")
        //Write out
        (histogram_overall.coalesce(1)
                            .write.format("com.databricks.spark.csv")
                            .option("header", "true")
                            .save(histogram_overall_write_path))
        
        //Histogram by edge (averages)
        val histogram_edge_avg = (delta_df.groupBy("edge").agg(avg("t_0_density"),
                                                                avg("t-1_delta"),
                                                                avg("t-2_delta"),
                                                                avg("t-3_delta"),
                                                                avg("tot_vehicle_count")))
        //Write out
        (histogram_edge_avg.coalesce(1)
                            .write.format("com.databricks.spark.csv")
                            .option("header", "true")
                            .save(histogram_edge_avg_write_path))

        //Histogram by interval (averages)
        val histogram_interval_avg = (delta_df.groupBy("interval").agg(avg("t_0_density"),
                                                                        avg("t-1_delta"),
                                                                        avg("t-2_delta"),
                                                                        avg("t-3_delta"),
                                                                        avg("tot_vehicle_count")))
        //Write out
        (histogram_interval_avg.coalesce(1)
                                .write.format("com.databricks.spark.csv")
                                .option("header", "true")
                                .save(histogram_interval_avg_write_path))

        //EDGE WEIGHT TIME SERIES (EDGE LEVEL)
        //Calculate average for density time series plot
        val edge_time_series = (delta_df.groupBy("interval").agg(avg("t_0_density"),
                                                                    avg("t-1_delta"),
                                                                    avg("t-2_delta"),
                                                                    avg("t-3_delta"),
                                                                    avg("tot_vehicle_count")))
        //Write out
        (edge_time_series.coalesce(1)
                            .write.format("com.databricks.spark.csv")
                            .option("header", "true")
                            .save(edge_time_series_write_path))

        //EDGE WEIGHT TIME SERIES (ROAD LEVEL)
        //Decompose from and to node columns into roads
        val density_with_roads = (delta_df.withColumn("from_ave", split(col("from"),"_")(0))
                                            .withColumn("from_st", split(col("from"),"_")(1))
                                            .withColumn("to_ave", split(col("to"),"_")(0))
                                            .withColumn("to_st", split(col("to"),"_")(1)))
        //Get avenue-level densities
        val ave_time_series = (density_with_roads.filter(col("from_ave") === col("to_ave"))
                                                    .drop("to_ave", "from_st", "to_st", "edge")
                                                    .withColumnRenamed("from_ave", "ave")
                                                    .groupBy("interval","ave")
                                                    .agg(
                                                        sum("tot_vehicle_count"),
                                                        sum("edge_area"))
                                                    .withColumn("agg_density", col("sum(tot_vehicle_count)").cast(FloatType) / col("sum(edge_area)").cast(FloatType))
                                                    .withColumnRenamed("sum(tot_vehicle_count)","agg_vehicle_count")
                                                    .drop("sum(edge_area)"))
        //Get street-level densities
        val st_time_series = (density_with_roads.filter(col("from_st") === col("to_st"))
                                                    .drop("to_ave", "from_ave", "to_st", "edge")
                                                    .withColumnRenamed("from_st", "st")
                                                    .groupBy("interval","st")
                                                    .agg(
                                                        sum("tot_vehicle_count"),
                                                        sum("edge_area"))
                                                    .withColumn("agg_density", col("sum(tot_vehicle_count)").cast(FloatType) / col("sum(edge_area)").cast(FloatType))
                                                    .withColumnRenamed("sum(tot_vehicle_count)","agg_vehicle_count")
                                                    .drop("sum(edge_area)"))
        //Write out
        (ave_time_series.coalesce(1)
                            .write.format("com.databricks.spark.csv")
                            .option("header", "true")
                            .save(ave_time_series_write_path))
        (st_time_series.coalesce(1)
                            .write.format("com.databricks.spark.csv")
                            .option("header", "true")
                            .save(st_time_series_write_path))
        
        val delta_df_unpersist = delta_df.unpersist

        //TOTAL TRIP TIME HISTOGRAM
        //Load trace file into dataframe from parquet file
        val total_trip_time = (spark.read.parquet(trace_file_path)
                                            .groupBy("id")
                                            .agg(
                                                max("time"),
                                                min("time")
                                            )
                                            .withColumnRenamed("max(time)","end_time")
                                            .withColumnRenamed("min(time)","start_time")
                                            .withColumn("total_trip_time", col("end_time")-col("start_time"))
                                            .drop("end_time", "start_time"))
        //Write out
        (total_trip_time.coalesce(1)
                            .write.format("com.databricks.spark.csv")
                            .option("header", "true")
                            .save(total_trip_time_write_path))

        spark.stop()
    }
}
