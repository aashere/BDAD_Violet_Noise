//ASSUME: edgeDensity has been run BEFORE this script
val max_density_df_write_path = ""
val max_density_df = (density_df.groupby("edge")
                                .max("density")
                                .as("max_density"))
//Write max_density_df to csv
(max_density_df.coalesce(1)
                .write.format("com.databricks.spark.csv")
                .option("header", "true")
                .save(max_density_df_write_path))