import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.types._
import org.apache.spark.sql.Column

// convert type Car1 and Car2 to type Car
val df = spark.read.format("csv").option("header",true).option("delimiter", ",").load("/user/jl11257/big_data_project/onedaydata/gps/part-00000-030816d8-9ea7-45ea-b877-6ddf7d9113cc-c000.csv")
df.show
val df_drop_columns_unused = df.drop("x", "y", "pos", "lane", "slope", "start", "stop")
df_drop_columns_unused.show
val df_convert_car_type = df_drop_columns_unused.withColumn("type", when($"type" === "Bus", $"type").otherwise(lit("Car")))
df_convert_car_type.show


// maxSpeed feature
val df_max_speed = df_convert_car_type.groupBy("id").agg(max("speed")).withColumnRenamed("max(speed)", "maxSpeed")
df_max_speed.show
val df_max_speed_feature = df_convert_car_type.join(df_max_speed, Seq("id"), "left_outer")
df_max_speed_feature.show

// convert time to hour and minute, choose the way according to the dataset of time(min) or time(second)
// 1.convert time(min) to hour and minute
val df_hour = df_max_speed_feature.withColumn("hour", ($"time" % (24 * 60)) / 60).withColumn("hour", col("hour").cast("int"))
df_hour.show
val df_hour_min = df_hour.withColumn("minute", ($"time" % (24 * 60)) % 60).withColumn("minute", col("minute").cast("int"))
df_hour_min.show

// 2.convert time(second) to hour and minute
val df_hour = df_max_speed_feature.withColumn("hour", ($"time" % (24 * 60 * 60)) / (60 * 60)).withColumn("hour", col("hour").cast("int"))
df_hour.show
val df_hour_min = df_hour.withColumn("minute", ($"time" % (24 * 60 * 60)) % (60 * 60)).withColumn("minute", col("minute").cast("int"))
df_hour_min.show


// angle feature -- to calculate the turns the vehicle makes by using count
val windowSpec = Window.partitionBy('id).orderBy('time)
val df_valid_angle = df_hour_min.filter(($"angle" === "0.00" ) || ($"angle" === "90.00" ) || ($"angle" === "180.00" ) || ($"angle" === "270.00" ))
val df_angle_minus = df_valid_angle.withColumn("angleLag", lag('angle, 1) over windowSpec).withColumn("angleMinus", $"angle" - $"angleLag")
df_angle_minus.show
// df_angle_minus.show(1000, false)

// val df_veh2036 = df_angle_minus.filter($"id" === "veh2036").collect.foreach(println)
// df_veh2036.show(843, false)
// df_veh2036.write.format("csv").option("header", "true").save("/user/jl11257/big_data_project/onedaydata/id_2036")

val df_angle_minus_filter = df_angle_minus.filter(!(isnull($"angleMinus")) && ($"angleMinus" !== 0))
// val df_veh2036 = df_angle_minus_filter.orderBy("time").filter($"id" === "veh2036").collect.foreach(println)
// val df_count = df_angle_minus_filter.groupBy("id").count().where('count < 2).withColumn("angleFeature", lit("1")).drop("count")
val df_count = df_angle_minus_filter.groupBy("id").count()
df_count.show

val df_angle_feature = df_hour_min.join(df_count, Seq("id"), "left_outer").withColumnRenamed("count", "turnsCount").drop("angle")
df_angle_feature.show

// count bus: df_bus_count: Long = 334 
// val df_bus_count = df.filter($"type" === "Bus").select("id", "type").distinct.count()


// use partitionby, but seems not work
// val df_partition_by_id = df.write.format("csv").option("header", "true").partitionBy("id").mode("overwrite").csv("/user/jl11257/big_data_project/onedaydata/gps_partitionby_id")
// val df_partition_by_id_order_by_time = df_partition_by_id.orderBy("time")

// val windowSpec = Window.partitionBy('id).orderBy('time)
// val df_valid_angle = df_partition_by_id_order_by_time.filter(($"angle" === "0.00" ) || ($"angle" === "90.00" ) || ($"angle" === "180.00" ) || ($"angle" === "270.00" ))
// val df_angle_minus = df_valid_angle.withColumn("angleLag", lag('angle, 1) over windowSpec).withColumn("angleMinus", $"angle" - $"angleLag")
// df_angle_minus.show

// val df_angle_minus_filter = df_angle_minus.filter(!(isnull($"angleMinus")) && ($"angleMinus" !== 0))
// val df_count = df_angle_minus_filter.groupBy("id").count()
// df_count.show

// val df_angle_feature = df_hour_min.join(df_count, Seq("id"), "left_outer").withColumnRenamed("count", "turnsCount").drop("angle")
// df_angle_feature.show


// use orderBy first, but seems not work
// val df_valid_angle = df_hour_min.filter(($"angle" === "0.00" ) || ($"angle" === "90.00" ) || ($"angle" === "180.00" ) || ($"angle" === "270.00" )).orderBy("id", "time")
// val df_angle_minus = df_valid_angle.withColumn("angleLag", lag('angle, 1) over windowSpec).withColumn("angleMinus", $"angle" - $"angleLag")
// df_angle_minus.show(1000, false)



// busstop feature(bus stops at busstop for 7min)
// val df_speed_sum = df_angle_feature.withColumn("speedLag1", lag('speed, 1) over windowSpec).withColumn("speedLag2", lag('speed, 2) over windowSpec).withColumn("speedLag3", lag('speed, 3) over windowSpec).withColumn("speedLag4", lag('speed, 4) over windowSpec).withColumn("speedLag5", lag('speed, 5) over windowSpec).withColumn("speedLag6", lag('speed, 6) over windowSpec).withColumn("speedLag7", lag('speed, 7) over windowSpec).withColumn("speedSum7", $"speed" + $"speedLag1" + $"speedLag2" + $"speedLag3" + $"speedLag4" + $"speedLag5" + $"speedLag6").withColumn("speedSum8", $"speed" + $"speedLag1" + $"speedLag2" + $"speedLag3" + $"speedLag4" + $"speedLag5" + $"speedLag6" + $"speedLag7")
// df_speed_sum.show

// val df_bus_stop_feature = df_speed_sum.withColumn("busStopFeature", when((($"speedSum7" === 0) && ($"speedSum8" !== 0)), "1").otherwise(lit("0"))).drop("speed", "speedLag1", "speedLag2", "speedLag3", "speedLag4", "speedLag5", "speedLag6", "speedLag7", "speedSum7", "speedSum8")
// df_bus_stop_feature.show

// busstop feature(bus stops at busstop for 7min)
val df_consecutive_zeros = df_angle_feature.withColumn("lag", lag("speed", 1) over windowSpec).withColumn("lead", lead("speed", 1) over windowSpec).withColumn("start", when((($"speed" === 0.00) && ($"speed" !== coalesce($"lag"))), $"time").otherwise(lit(null))).withColumn("end", when((($"speed" === 0.00) && ($"speed" !== coalesce($"lead"))), $"time").otherwise(lit(null))).orderBy("id", "time")
df_consecutive_zeros.show
// df_consecutive_zeros.show(843, false)

// val df_start = df_consecutive_zeros.filter(!(isnull($"start"))).orderBy("id", "time").withColumnRenamed("id", "startId").withColumnRenamed("time", "startTime").drop("type", "start_vertex_id", "stop_vertex_id", "maxSpeed", "hour", "minute", "turnsCount", "speed","lag", "lead", "end")
// df_start.show
// val df_end = df_consecutive_zeros.filter(!(isnull($"end"))).orderBy("id", "time").withColumnRenamed("id", "endId").withColumnRenamed("time", "endTime").drop("type", "start_vertex_id", "stop_vertex_id", "maxSpeed", "hour", "minute", "turnsCount", "speed", "lag", "lead", "start")
// df_end.show
// val df_start_end = df_start.join(df_end, Seq("id"), "left_outer").orderBy("id", "startTime", "endTime")
// df_start_end.show
// val df_consecutive_zeros_count = df_start_end.withColumn("consecutiveZerosCount", $"endTime" - $"startTime" + 1)
// df_consecutive_zeros_count.show

// val df_1 = df_angle_feature.join(df_start, $"id" === $"startId" && ($"time" === $"startTime"), "leftouter").orderBy("id", "time").drop("startId", "startTime")
// df_1.show
// val df_2 = df_1.join(df_end, $"id" === $"endId" && ($"time" === $"endTime"), "leftouter").drop("endId", "endTime")
// df_2.show

// val windowSpec1 = Window.partitionBy('id, 'speed)
// val df1 = df_consecutive_zeros.withColumn("consecutiveZerosCount", ((max("end") over windowSpec1) - (min("start") over windowSpec1))).orderBy("id", "time")
// df1.show(843, false)



// save as csv file
// df_bus_stop_feature.write.format("csv").option("header", "true").save("/user/jl11257/big_data_project/onedaydata/gps_preprocess_data")