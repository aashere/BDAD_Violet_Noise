//ASSUME: fullDensity has been run in the shell BEFORE this script
import org.apache.spark.sql.expressions.Window
val delta_df_write_path = "/user/jl11257/big_data_project/features/regression"
val delta_hist_df_write_path = "/user/jl11257/big_data_project/visualizations/delta_histogram"
val density_time_series_df_write_path = "/user/jl11257/big_data_project/visualizations/edge_time_series"
val ave_density_df_write_path = "/user/jl11257/big_data_project/visualizations/road_time_series/ave"
val st_density_df_write_path = "/user/jl11257/big_data_project/visualizations/road_time_series/st"

val DELTA_VALUE = 15*60
//DELTAS FEATURE TABLE
//Roll back data to delta-sized intervals
val density_df = (full_density_df.withColumn("interval", (col("time").cast(IntegerType) / DELTA_VALUE).cast(IntegerType))
                                    .groupBy("interval", "edge")
                                    .agg(
                                        sum("num_car1"),
                                        sum("num_car2"),
                                        sum("num_car3"),
                                        sum("num_bus"),
                                        sum("num_vehicles"),
                                        sum("density"))
                                    .withColumnRenamed("sum(num_car1)", "tot_car1_count")
                                    .withColumnRenamed("sum(num_car2)", "tot_car2_count")
                                    .withColumnRenamed("sum(num_car3)", "tot_car3_count")
                                    .withColumnRenamed("sum(num_bus)", "tot_bus_count")
                                    .withColumnRenamed("sum(num_vehicles)", "tot_vehicle_count")
                                    .withColumnRenamed("sum(density)", "tot_density")
                                    .join(edge_areas_df, col("edge") === edge_areas_df.col("edge_id"))
                                    .drop("edge_id").cache())
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
                            .drop("t-3_lag"))
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
