import org.apache.spark._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.Column
val node_table_path = "/user/jl11257/big_data_project/graph/node_table.csv"
val edge_table_path = "/user/jl11257/big_data_project/graph/edge_table.csv"
val trace_file_path = "/user/jl11257/big_data_project/traces/processed/*"
val NUM_DAYS = 70
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
//Load trace file into dataframe from parquet file
val trace_file_df = (spark.read.parquet(trace_file_path)
                                .withColumn("edge", expr("substring(lane, 0, length(lane)-2)")))
//Get number of cars and number of buses
val num_car1_df = (trace_file_df.filter(col("type") === "Car1")
                                .groupBy("time","edge")
                                .count()
                                .withColumnRenamed("count", "num_car1"))
val num_car2_df = (trace_file_df.filter(col("type") === "Car2")
                                .groupBy("time","edge")
                                .count()
                                .withColumnRenamed("count", "num_car2"))
val num_car3_df = (trace_file_df.filter(col("type") === "Car3")
                                .groupBy("time","edge")
                                .count()
                                .withColumnRenamed("count", "num_car3"))
val num_bus_df = (trace_file_df.filter(col("type") === "Bus")
                                .groupBy("time","edge")
                                .count()
                                .withColumnRenamed("count","num_bus"))
//Join them together into one vehicle count
val num_car_1_2_df = (num_car1_df.join(num_car2_df, Seq("time","edge"),"fullouter")
                                    .na.fill(0)
                                    .withColumn("num_car_1_2", col("num_car1")*4+col("num_car2")*5))
val num_car_1_2_3_df = (num_car_1_2_df.join(num_car3_df, Seq("time","edge"),"fullouter")
                                    .na.fill(0)
                                    .withColumn("num_car_1_2_3", col("num_car_1_2")+col("num_car3")*7)
                                    .drop("num_car_1_2"))
val vehicle_count_df = (num_car_1_2_3_df.join(num_bus_df, Seq("time","edge"),"fullouter")
                                    .na.fill(0)
                                    .withColumn("num_vehicles", col("num_car_1_2_3")+col("num_bus")*15)
                                    .drop("num_car_1_2_3"))
//Pull out time column into day and time_of_day (in seconds, within the day) and repartition on day
val vehicle_count_partition = (vehicle_count_df.withColumn("day", (col("time").cast(IntegerType) / 86400).cast(IntegerType))
                                                        .withColumn("time_of_day", col("time")%86400)
                                                        .drop("time")
                                                        .orderBy("day","time_of_day")
                                                        .repartition(col("day")))
//Create a dataframe with day 0 to NUM_DAYS-1
val days_arr: Array[Int] = new Array[Int](NUM_DAYS)
var i = 0
for(i<-0 to NUM_DAYS-1){
    days_arr(i) = i
}
val days_df = sc.parallelize(days_arr).toDF("day")
//Create a dataframe with time_of_day 0 to 86399
val timestamps_arr: Array[Int] = new Array[Int](86400)
i = 0
for(i<-0 to 86399){
    timestamps_arr(i) = i
}
val timestamps_df = sc.parallelize(timestamps_arr).toDF("time_of_day")
//Take caretesian product and broadcast out df
val time_edge_df = (days_df.crossJoin(timestamps_df)
                        .crossJoin(edge_areas_df)
                        .withColumnRenamed("edge_id", "edge"))
val broadcastVar = sc.broadcast(time_edge_df)
//Join the missing edges onto the full density dataframe, fill nulls and recover time column, add density
//Note that since this is a right join, we are dropping the internal ":" edges
//Cache full_density_df, since it is used in every other summary stat calculation
val full_density_df = (vehicle_count_partition.join(broadcastVar.value, Seq("day","time_of_day", "edge"), "rightouter")
                                                .na.fill(0)
                                                .withColumn("time", col("day")*86400+col("time_of_day"))
                                                .drop("day","time_of_day")
                                                .withColumn("density", col("num_vehicles").cast(FloatType) / col("edge_area").cast(FloatType))
                                                .cache())