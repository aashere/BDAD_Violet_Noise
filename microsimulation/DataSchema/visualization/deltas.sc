//ASSUME: edgeDensity has been run BEFORE this script
//Join from and to node id's onto density_df and add t0 (current density)
val delta_df_write_path = ""
val delta_value = 1800
val windowSpec = Window.partitionBy("edge").orderBy("time")
//Join from and to node id's and add the windowing
val delta_df = density_df.join(edge_df, density_df.col("edge") === edge_df.col("edge_id"))
                                    .drop("edge_id")
                                    .withColumnRenamed("density", "t_0_density")
                                    .withColumn("t-1_lag", lag("t_0_density",delta_value).over(windowSpec))
                                    .withColumn("t-1_delta", col("t_0_density")-col("t-1_lag"))
                                    .drop("t-1_lag")
                                    .withColumn("t-2_lag", lag("t_0_density",delta_value*2).over(windowSpec))
                                    .withColumn("t-2_delta", col("t_0_density")-col("t-2_lag"))
                                    .drop("t-2_lag")
                                    .withColumn("t-3_lag", lag("t_0_density",delta_value*3).over(windowSpec))
                                    .withColumn("t-3_delta", col("t_0_density")-col("t-3_lag"))
                                    .drop("t-3_lag"))
//Write delta_df to csv
(delta_df.coalesce(1)
            .write.format("com.databricks.spark.csv")
            .option("header", "true")
            .save(delta_df_write_path))