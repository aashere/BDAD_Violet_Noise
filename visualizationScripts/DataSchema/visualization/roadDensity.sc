//ASSUME: edgeDensity has been run BEFORE this script
val ave_density_df_write_path = ""
val st_density_df_write_path = ""
//Decompose edge column into roads
val density_with_roads_df = (vehicle_count_df.join(edge_lengths_df, vehicle_count_df.col("edge") === edge_lengths_df.col("edge_id"))
                                        .drop("edge_id")
                                        .withColumn("from_ave", split(split(col("edge"),"(?<!g)(to)(?!n)")(0),"_")(0))
                                        .withColumn("from_st", split(split(col("edge"),"(?<!g)(to)(?!n)")(0),"_")(1))
                                        .withColumn("to_ave", split(split(col("edge"),"(?<!g)(to)(?!n)")(1),"_")(0))
                                        .withColumn("to_st", split(split(col("edge"),"(?<!g)(to)(?!n)")(1),"_")(1)))
//Get avenue-level densities
val ave_density_df = (density_with_roads_df.filter(col("from_ave") === col("to_ave"))
                                            .drop("to_ave", "from_st", "to_st", "edge")
                                            .withColumnRenamed("from_ave", "ave")
                                            .groupby("ave")
                                            .agg(
                                                sum("num_vehicles").as("ave_num_vehicles"),
                                                sum("edge_length").as("ave_length"))
                                            .withColumn("density", col("ave_num_vehicles").cast(FloatType) / col("ave_length").cast(FloatType))
                                            .drop("ave_num_vehicles","ave_length"))
//Get street-level densities
val st_density_df = (density_with_roads_df.filter(col("from_st") === col("to_st"))
                                            .drop("to_st", "from_ave", "to_ave", "edge")
                                            .withColumnRenamed("from_st", "st")
                                            .groupby("st")
                                            .agg(
                                                sum("num_vehicles").as("st_num_vehicles"),
                                                sum("edge_length").as("st_length"))
                                            .withColumn("density", col("st_num_vehicles").cast(FloatType) / col("st_length").cast(FloatType))
                                            .drop("st_num_vehicles","st_length"))
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