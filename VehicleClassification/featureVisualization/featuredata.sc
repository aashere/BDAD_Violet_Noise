// using data from training data
val carFileName = "/user/jl11257/big_data_project/predictions/vehicleClassification/predictionWholeData/carDownSamping"
val busFileName = "/user/jl11257/big_data_project/predictions/vehicleClassification/predictionWholeData/bus"
val carInfo = spark.read.option("header",true).option("delimiter", ",").option("mode", "DROPMALFORMED").csv(carFileName)
val busInfo = spark.read.option("header",true).option("delimiter", ",").option("mode", "DROPMALFORMED").csv(busFileName)
val vehicleInfo = carInfo.unionAll(busInfo)
vehicleInfo.write.format("csv").option("header", "true").save("/user/jl11257/big_data_project/testing/vehicleClassification/featuredata")

// calculate the percentage of car and bus to do normalization in feature visualization
val df1 = spark.read.format("csv").option("header", "true").load("/user/jl11257/big_data_project/testing/vehicleClassification/featuredata/part-00000-10cde96a-499b-4a6f-a725-b046543539cc-c000.csv")
df1.filter(col("type") === "Car").count()
df1.filter(col("type") === "Bus").count()