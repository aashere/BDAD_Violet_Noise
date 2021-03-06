// using data from training data
val carFileName = "/user/jl11257/big_data_project/predictions/vehicleClassification/predictionWholeData/carDownSamping"
val busFileName = "/user/jl11257/big_data_project/predictions/vehicleClassification/predictionWholeData/bus"
val carInfo = spark.read.option("header",true).option("delimiter", ",").option("mode", "DROPMALFORMED").csv(carFileName)
val busInfo = spark.read.option("header",true).option("delimiter", ",").option("mode", "DROPMALFORMED").csv(busFileName)
val vehicleInfo = carInfo.unionAll(busInfo)

// downsampled data
val originaldata = "/user/jl11257/big_data_project/features/vehiclesample/training"
val noisedata = "/user/jl11257/big_data_project/features/vehiclesamplenoise/training"

(spark.read.parquet(originaldata).coalesce(1).write.format("csv").option("header", "true")
    .save("/user/jl11257/big_data_project/visualizations/classifycar_featuresorig"))
(spark.read.parquet(noisedata).coalesce(1).write.format("csv").option("header", "true")
    .save("/user/jl11257/big_data_project/visualizations/classifycar_featuresnoise"))

vehicleInfo.write.format("csv").option("header", "true").save("/user/jl11257/big_data_project/visualizations/vehicleClassification/featuredata")

// calculate the percentage of car and bus to do normalization in feature visualization
val df1 = spark.read.format("csv").option("header", "true").load("/user/jl11257/big_data_project/visualizations/vehicleClassification/featuredata")
df1.filter(col("type") === "Car").count()
df1.filter(col("type") === "Bus").count()