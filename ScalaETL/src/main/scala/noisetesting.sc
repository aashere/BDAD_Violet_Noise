val affected_vehicles = (trace_file_df.filter((col("day") === last_day) && 
                                      (col("time_of_day")>=accident_start_time) && 
                                      (col("time_of_day")<=accident_end_time) && 
                                      (col("to_node") === accident_node(0))))

// need to actually join back against the trace file df to get the full remaining trace for the vehicle so it can be shifted back
val joinback = affected_vehicles.select("id").distinct.withColumn("accidentStart", lit(accident_start_time))
val traces_to_mod = trace_file_df.join(joinback, Seq("id")).filter(col("time_of_day") >= accident_start_time)

val test = traces_to_mod.filter(col("id") === "veh6232594")
test.cache

test.count()

val repeatrec = (test.groupBy("id")
 					.agg(min("time_of_day"))
 					.withColumnRenamed("min(time_of_day)","time_of_day")
 					.withColumn("addDelay",col("time_of_day") - accident_end_time) // this is how many repeats we insert
 					.withColumn("addDelay", col("addDelay") * lit(-1))
 					.withColumn("duplicate", lit(1)))

val rowchoice = (test.join(repeatrec.select("id", "time_of_day", "duplicate"), Seq("id", "time_of_day"), "leftouter").na.fill(0)
 					 .join(repeatrec.select("id", "addDelay"), Seq("id")))

val dummyArray = udf((d: Int) => (0 until d + 1).toArray)
spark.udf.register("dummyArray",dummyArray)

val repeats = (rowchoice.filter(col("duplicate")===1)
						.withColumn("dummy",explode(dummyArray(col("addDelay")))) // generate repeated rows
						.withColumn("new_time", col("time") + col("dummy"))
						.drop("dummy"))
val regulars = rowchoice.filter(col("duplicate")===0).withColumn("new_time", col("time") + col("addDelay")) // push back times on other rows for vehicle

val results = repeats.union(regulars)