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

        val node_table_path = "/user/jl11257/big_data_project/graph/node_table.csv"
        val edge_table_path = "/user/jl11257/big_data_project/graph/edge_table.csv"
        val trace_file_path = args(0)
        val NUM_DAYS = args(1).toInt
        //Load node_table data into dataframe from csv
        val node_df = (spark.read.format("csv")
                                    .option("header", "true")
                                    .load(node_table_path)
                                    .withColumnRenamed("id", "node_id")
                                    .drop("type","latitude","longitude"))
        //Load edge_table data into dataframe from csv
        val edge_df = (spark.read.format("csv")
                                    .option("header","true")
                                    .load(edge_table_path)
                                    .withColumnRenamed("id", "edge_id")
                                    .withColumn("numLanes", expr("substring(type, 0, 1)"))
                                    .drop("type"))
        //Get edge areas
        val edge_areas_df = (edge_df.join(node_df, edge_df.col("from") === node_df.col("node_id"))
                                    .drop("node_id")
                                    .withColumnRenamed("x", "from_x")
                                    .withColumnRenamed("y", "from_y")
                                    .join(node_df, edge_df.col("to") === node_df.col("node_id"))
                                    .drop("node_id")
                                    .withColumnRenamed("x", "to_x")
                                    .withColumnRenamed("y", "to_y")
                                    .withColumn("edge_length", sqrt(pow(col("to_y")-col("from_y"),2)+pow(col("to_x")-col("from_x"),2)))
                                    .withColumn("edge_area", col("edge_length")*col("numLanes"))
                                    .drop("from","to","from_x","from_y","to_x","to_y"))
        //Load trace file into dataframe from parquet file and get vehicle count
        val trace_file_df = (spark.read.parquet(trace_file_path)
                                        .withColumn("edge", expr("substring(lane, 0, length(lane)-2)")))
                                        
        val vehicle_count_df = (trace_file_df.groupBy("time","edge")
                                    .pivot("type", Seq("Car1","Car2","Car3","Bus"))
                                    .count()
                                    .na.fill(0)
                                    .withColumn("num_vehicles", col("Car1")*4+col("Car2")*5+col("Car3")*7+col("Bus")*15))

        //Pull out time column into day and time_of_day (in seconds, within the day) and repartition on day
        val vehicle_count_partition = (vehicle_count_df.withColumn("day", (col("time").cast(IntegerType) / 86400).cast(IntegerType))
                                                                .withColumn("time_of_day", col("time")%86400)
                                                                .drop("time")
                                                                .orderBy("day","time_of_day")
                                                                .repartition(col("day"),col("time_of_day"),col("edge")))
        //Create a dataframe with day 0 to NUM_DAYS-1
        val days_arr: Array[Int] = new Array[Int](NUM_DAYS)
        var i = 0
        for(i<-0 to NUM_DAYS-1){
            days_arr(i) = i
        }
        val days_df = spark.sparkContext.parallelize(days_arr).toDF("day")
        //Create a dataframe with time_of_day 0 to 86399
        val timestamps_arr: Array[Int] = new Array[Int](86400)
        i = 0
        for(i<-0 to 86399){
            timestamps_arr(i) = i
        }
        val timestamps_df = spark.sparkContext.parallelize(timestamps_arr).toDF("time_of_day")
        //Take cartesian product
        val day_time_edge_df = (days_df.crossJoin(timestamps_df)
                                        .crossJoin(edge_areas_df)
                                        .withColumnRenamed("edge_id", "edge")
                                        .repartition(col("day"),col("time_of_day"),col("edge")))
        //Join the missing edges onto the full density dataframe, fill nulls and recover time column, add density
        //Note that since this is a right join, we are dropping the internal ":" edges
        val full_density_df = (vehicle_count_partition.join(day_time_edge_df, Seq("day","time_of_day", "edge"), "rightouter")
                                                        .na.fill(0)
                                                        .withColumn("time", col("day")*86400+col("time_of_day"))
                                                        .drop("day","time_of_day")
                                                        .withColumn("density", col("num_vehicles").cast(FloatType) / col("edge_area").cast(FloatType)))

        val delta_df_write_path = "/user/jl11257/big_data_project/features/regression"
        val delta_hist_df_write_path = "/user/jl11257/big_data_project/visualizations/delta_histogram"
        val density_time_series_df_write_path = "/user/jl11257/big_data_project/visualizations/edge_time_series"
        val ave_density_df_write_path = "/user/jl11257/big_data_project/visualizations/road_time_series/ave"
        val st_density_df_write_path = "/user/jl11257/big_data_project/visualizations/road_time_series/st"

        val DELTA_VALUE = args(2).toInt
        //DELTAS FEATURE TABLE
        //Roll back data to delta-sized intervals
        val density_df = (full_density_df.withColumn("interval", (col("time").cast(IntegerType) / DELTA_VALUE).cast(IntegerType))
                                    .groupBy("interval", "edge")
                                    .agg(
                                        sum("Car1"),
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
                                    .withColumnRenamed("sum(density)", "tot_density")
                                    .join(edge_areas_df, col("edge") === edge_areas_df.col("edge_id"))
                                    .drop("edge_id")
                                    .cache)
        val windowSpec = Window.partitionBy("edge").orderBy("interval")
        //Add from and to node id's and windowing
        val delta_df = (density_df.withColumn("from",split(col("edge"),"(?<!g)(to)(?!n)")(0))
                                    .withColumn("to",split(col("edge"),"(?<!g)(to)(?!n)")(1))
                                    .withColumnRenamed("tot_density", "t_0_density")
                                    .withColumn("t-1_lag", lag("t_0_density",1).over(windowSpec))
                                    .withColumn("t-1_delta", col("t_0_density")-col("t-1_lag"))
                                    .drop("t-1_lag")
                                    .withColumn("t-2_lag", lag("t_0_density",2).over(windowSpec))
                                    .withColumn("t-2_delta", col("t_0_density")-col("t-2_lag"))
                                    .drop("t-2_lag")
                                    .withColumn("t-3_lag", lag("t_0_density",3).over(windowSpec))
                                    .withColumn("t-3_delta", col("t_0_density")-col("t-3_lag"))
                                    .drop("t-3_lag")
                                    .withColumn("mod_week", (col("interval")*DELTA_VALUE+DELTA_VALUE-1) % (60*60*24*7))
                                    .withColumn("mod_day", col("mod_week") % (60*60*24))
                                    .withColumn("mod_hour", col("mod_day") % (60*60))
                                    .withColumn("week", ((col("interval")*DELTA_VALUE+DELTA_VALUE-1) / (60*60*24*7)).cast(IntegerType))
                                    .withColumn("day_of_week", (col("mod_week") / (60*60*24)).cast(IntegerType))
                                    .withColumn("hour_of_day", (col("mod_day") / (60*60)).cast(IntegerType))
                                    .withColumn("minute_of_hour", (col("mod_hour") / 60).cast(IntegerType))
                                    .drop("mod_week","mod_day","mod_hour")
                                    .cache)
        //Write deltas feature table to csv
        (delta_df.coalesce(1)
                    .write.format("com.databricks.spark.csv")
                    .option("header", "true")
                    .save(delta_df_write_path))

        //DELTAS HISTOGRAM
        //Do histogram calculations
        val delta_hist_df = (delta_df.groupBy("edge").agg(max("t_0_density"),
                                                            min("t_0_density"),
                                                            avg("t_0_density"),
                                                            max("t-1_delta"),
                                                            min("t-1_delta"),
                                                            avg("t-1_delta"),
                                                            max("t-2_delta"),
                                                            min("t-2_delta"),
                                                            avg("t-2_delta"),
                                                            max("t-3_delta"),
                                                            min("t-3_delta"),
                                                            avg("t-3_delta"),
                                                            max("tot_vehicle_count"),
                                                            min("tot_vehicle_count"),
                                                            avg("tot_vehicle_count")))
        //Write delta_hist_df to csv
        (delta_hist_df.coalesce(1)
                        .write.format("com.databricks.spark.csv")
                        .option("header", "true")
                        .save(delta_hist_df_write_path))

        val delta_df_unpersist = delta_df.unpersist

        //EDGE WEIGHT TIME SERIES (EDGE LEVEL)
        //Calculate average for density time series plot
        val density_time_series_df = (density_df.groupBy("interval").agg(avg("tot_density"),
                                                                            avg("tot_vehicle_count")))

        //Write to csv
        (density_time_series_df.coalesce(1)
                                .write.format("com.databricks.spark.csv")
                                .option("header", "true")
                                .save(density_time_series_df_write_path))

        //EDGE WEIGHT TIME SERIES (ROAD LEVEL)
        //Decompose edge column into roads
        val density_with_roads_df = (density_df.withColumn("from_ave", split(split(col("edge"),"(?<!g)(to)(?!n)")(0),"_")(0))
                                                .withColumn("from_st", split(split(col("edge"),"(?<!g)(to)(?!n)")(0),"_")(1))
                                                .withColumn("to_ave", split(split(col("edge"),"(?<!g)(to)(?!n)")(1),"_")(0))
                                                .withColumn("to_st", split(split(col("edge"),"(?<!g)(to)(?!n)")(1),"_")(1)))
        //Get avenue-level densities
        val ave_density_df = (density_with_roads_df.filter(col("from_ave") === col("to_ave"))
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
        val st_density_df = (density_with_roads_df.filter(col("from_st") === col("to_st"))
                                                    .drop("to_ave", "from_ave", "to_st", "edge")
                                                    .withColumnRenamed("from_st", "st")
                                                    .groupBy("interval","st")
                                                    .agg(
                                                        sum("tot_vehicle_count"),
                                                        sum("edge_area"))
                                                    .withColumn("agg_density", col("sum(tot_vehicle_count)").cast(FloatType) / col("sum(edge_area)").cast(FloatType))
                                                    .withColumnRenamed("sum(tot_vehicle_count)","agg_vehicle_count")
                                                    .drop("sum(edge_area)"))

        //Write avenue-level density to csv
        (ave_density_df.coalesce(1)
                        .write.format("com.databricks.spark.csv")
                        .option("header", "true")
                        .save(ave_density_df_write_path))
        //Write street-level density to csv
        (st_density_df.coalesce(1)
                        .write.format("com.databricks.spark.csv")
                        .option("header", "true")
                        .save(st_density_df_write_path))
        
        val density_df_unpersist = density_df.unpersist

        val total_trip_time_df_write_path = "/user/jl11257/big_data_project/visualizations/total_trip_time"
        //TOTAL TRIP TIME HISTOGRAM
        //Get total trip times for each vehicle id
        val total_trip_time_df = (trace_file_df.groupBy("id")
                                        .agg(
                                            max("time"),
                                            min("time")
                                        )
                                        .withColumnRenamed("max(time)","end_time")
                                        .withColumnRenamed("min(time)","start_time")
                                        .withColumn("total_trip_time", col("end_time")-col("start_time"))
                                        .drop("end_time", "start_time"))
        //Write total_trip_time_df to csv
        (total_trip_time_df.coalesce(1)
                            .write.format("com.databricks.spark.csv")
                            .option("header", "true")
                            .save(total_trip_time_df_write_path))

        spark.stop()
    }
}
