//ASSUME: edgeDensity has been run BEFORE this script
val total_trip_time_df_write_path = ""
//Get start and end times for each vehicle id
val end_time_df = (trace_file_df.groupby("id")
                                .max("time")
                                .as("end_time"))
val start_time_df = (trace_file_df.groupby("id")
                                    .min("time")
                                    .as("start_time"))
//Calculate total trip time for each vehicle id
val total_trip_time_df = (end_time_df.join(start_time_df, Seq("id"))
                                        .withColumn("total_trip_time", col("end_time")-col("start_time"))
                                        .drop("end_time", "start_time"))
//Write total_trip_time_df to csv
(total_trip_time_df.coalesce(1)
                    .write.format("com.databricks.spark.csv")
                    .option("header", "true")
                    .save(total_trip_time_df_write_path))