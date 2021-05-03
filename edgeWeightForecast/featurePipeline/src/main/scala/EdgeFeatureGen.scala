import org.apache.spark._
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.Column
import com.databricks.spark._
import org.apache.spark.sql.expressions.Window


object EdgeFeatureGen {
    def main(args: Array[String]) {
        val spark = SparkSession.builder().appName("EdgeFeatureGen").getOrCreate
        import spark.implicits._

        val trace_file_path = args(0)
        val delta_df_write_path = args(1)
        val DELTA_VALUE = args(2).toInt
        val write_partitions = args(3).toInt

        val edge_areas_path = "/user/jl11257/big_data_project/graph/edge_area"
        val vertices_path = "/user/jl11257/big_data_project/graph/vertices"

        //Load edge area for density calc
        val edge_areas_df = spark.read.parquet(edge_areas_path)
        val vertices_df = spark.read.parquet(vertices_path)

        val edge_summary = (edge_areas_df.join(vertices_df, Seq("edge_id"), "inner")
                                         .drop("from_vertex_id").drop("to_vertex_id")
                                         .withColumnRenamed("edge_id", "edge")
                                         .repartition(col("edge")))

        //Load trace file into dataframe from parquet file and get vehicle count
        val trace_file_df = (spark.read.parquet(trace_file_path)
                                        .withColumn("edge", expr("substring(lane, 0, length(lane)-2)")))
                                        
        val vehicle_count_df = (trace_file_df.groupBy("edge","time")
                                    .pivot("type", Seq("Car1","Car2","Car3","Bus"))
                                    .count()
                                    .na.fill(0)
                                    .withColumn("num_vehicles", col("Car1")*4+col("Car2")*5+col("Car3")*7+col("Bus")*15)
                                    .repartition(col("edge")))

        //Create a dataframe that covers the min and max range of time for the data frame
        val timerange = trace_file_df.agg(min("time"), max("time")).collect()(0)
        val starttm : Int = timerange.getInt(0)
        val totalseconds : Int = timerange.getInt(1) - starttm
        val times_arr: Array[Int] = new Array[Int](totalseconds + 1)

        var i = 0
        for(i<-0 to totalseconds){
            times_arr(i) = starttm + i    
        }

        val timestamps_df = spark.sparkContext.parallelize(times_arr).toDF("time")

        //Take cartesian product
        val day_time_edge_df = edge_summary.crossJoin(timestamps_df).repartition(col("edge"))                          
        
        //Join the missing edges onto the full density dataframe, fill nulls and recover time column, add density
        //Note that since this is a right join, we are dropping the internal ":" edges
        val full_density_df = (vehicle_count_df.join(day_time_edge_df, Seq("edge", "time"), "rightouter")
                                                .na.fill(0)
                                                .withColumn("density", col("num_vehicles").cast(FloatType) / col("edge_area").cast(FloatType))
                                                .withColumn("interval", (col("time").cast(IntegerType) / DELTA_VALUE).cast(IntegerType)))


        //DELTAS FEATURE TABLE
        //Roll back data to delta-sized intervals
        val density_df = (full_density_df.groupBy("edge", "from", "to", "numLanes", "edge_length", "edge_area", "interval")
                                            .agg(sum("Car1"),
                                                sum("Car2"),
                                                sum("Car3"),
                                                sum("Bus"),
                                                sum("num_vehicles"),
                                                sum("density"))
                                            .withColumnRenamed("sum(Car1)", "tot_car1_count")
                                            .withColumnRenamed("sum(Car2)", "tot_car2_count")
                                            .withColumnRenamed("sum(Car3)", "tot_car3_count")
                                            .withColumnRenamed("sum(Bus)", "tot_bus_count")
                                            .withColumnRenamed("sum(num_vehicles)", "tot_vehicle_count")
                                            .withColumnRenamed("sum(density)", "t_0_density"))

        val density_time_df = (density_df.withColumn("mod_week", (col("interval")*DELTA_VALUE+DELTA_VALUE-1) % (60*60*24*7))
                                    .withColumn("mod_day", col("mod_week") % (60*60*24))
                                    .withColumn("mod_hour", col("mod_day") % (60*60))
                                    .withColumn("week", ((col("interval")*DELTA_VALUE+DELTA_VALUE-1) / (60*60*24*7)).cast(IntegerType))
                                    .withColumn("day_of_week", (col("mod_week") / (60*60*24)).cast(IntegerType))
                                    .withColumn("hour_of_day", (col("mod_day") / (60*60)).cast(IntegerType))
                                    .withColumn("minute_of_hour", (col("mod_hour") / 60).cast(IntegerType))
                                    .drop("mod_week","mod_day","mod_hour"))

        val windowSpec = Window.partitionBy("edge").orderBy("interval")
        //Add from and to node id's and windowing
        val delta_df = (density_time_df.withColumn("t-1_lag", lag("t_0_density",1).over(windowSpec))
                                    .withColumn("t-1_delta", col("t_0_density")-col("t-1_lag"))
                                    .drop("t-1_lag")
                                    .withColumn("t-2_lag", lag("t_0_density",2).over(windowSpec))
                                    .withColumn("t-2_delta", col("t_0_density")-col("t-2_lag"))
                                    .drop("t-2_lag")
                                    .withColumn("t-3_lag", lag("t_0_density",3).over(windowSpec))
                                    .withColumn("t-3_delta", col("t_0_density")-col("t-3_lag"))
                                    .drop("t-3_lag"))

        //Write deltas feature table 
        delta_df.coalesce(write_partitions).write.mode("append").parquet(delta_df_write_path)

        spark.stop()
    }
}