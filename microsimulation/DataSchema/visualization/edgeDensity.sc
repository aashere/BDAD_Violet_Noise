import org.apache.spark._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.Column

val node_table_path = ""
val edge_table_path = ""
val trace_file_path = ""
val density_df_write_path = ""

//Load node_table data into dataframe from csv
val node_df = (spark.read.format("csv")
			.option("header", "true")
			.load(node_path)
			.withColumnRenamed("id", "node_id")
            .drop("type","latitude","longitude"))
//Load edge_table data into dataframe from csv
val edge_df = (spark.read.format("csv")
			.option("header","true")
			.load(edge_path)
			.withColumnRenamed("id", "edge_id")
            .drop("type"))
//Get edge lengths
val edge_lengths_df = (edge_df.join(node_df, edge_df.col("from") === node_df.col("node_id"))
                            .drop("node_id")
                            .withColumnRenamed("x", "from_x")
                            .withColumnRenamed("y", "from_y")
                            .join(node_df, edge_df_from.col("to") === node_df.col("node_id"))
                            .drop("node_id")
                            .withColumnRenamed("x", "to_x")
                            .withColumnRenamed("y", "to_y")
                            .withColumn("edge_length", sqrt(pow(col("to_y")-col("from_y"),2)+pow(col("from_y")-col("from_x"),2)))
                            .drop("from","to","from_x","from_y","to_x","to_y"))
//Load trace file into dataframe from parquet file
val trace_file_df = (spark.read.parquet(trace_file_path)
                                .withColumn("edge", substring(col("lane"),0,length(col("lane")-2))))
//Get number of cars and number of buses
val num_cars_df = (trace_file_df.filter(lower(col("type")).contains("car"))
                                .groupby("time","edge")
                                .count()
                                .as("num_cars"))
val num_bus_df = (trace_file_df.filter(lower(col("type")).contains("bus"))
                                .groupby("time","edge")
                                .count()
                                .as("num_bus"))
//Join them together into one vehicle count
val vehicle_count_df = (num_cars_df.join(num_bus_df, Seq("time","edge"),"fullouter")
                                    .na.fill(0,Array("num_cars","num_bus"))
                                    .withColumn("num_vehicles", col("num_cars")+col("num_bus")*4)
                                    .drop("num_cars","num_bus"))
//Get densities
val density_df = (vehicle_count_df.join(edge_lengths_df, vehicle_count_df.col("edge") === edge_lengths_df.col("edge_id"))
                                    .drop("edge_id")
                                    .withColumn("density", col("num_vehicles").cast(FloatType) / col("edge_length").cast(FloatType))
                                    .drop("num_vehicles","edge_length"))
//Write density_df to csv
(density_df.coalesce(1)
            .write.format("com.databricks.spark.csv")
            .option("header", "true")
            .save(density_df_write_path))