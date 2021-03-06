import org.apache.spark.ml.tuning.CrossValidatorModel
import org.apache.spark.sql.types._

// load CrossValidatorModel model here 
val cvModelWithauROCLoaded = CrossValidatorModel.load("/user/jl11257/big_data_project/modelSaving/vehicleClassification/cvModelWithauROC")

// sample data(right now, use the data from the generated data. we could change it to new generated data)
// the vehicle type of four four rows of data is Bus, and the last four rows of data is Car
val df1 = spark.read.parquet("/user/jl11257/big_data_project/features/vehicleclass/part-00000-f8c4250a-43a1-4003-aa7d-ecf08b452f25-c000.snappy.parquet") 
val df2 = df1.drop("type")

// val df1 = Seq(
//   ("veh94177", "Bus", 27778, 0.0, 77, 76, 8.0, 1.59, 7, 42, 0),
//   ("veh5626547", "Bus", 5143831, 0.0, 45, 46, 8.0, 1.25, 12, 50, 1),
//   ("veh94288", "Bus", 63305, 0.0, 151, 152, 8.0, 1.27, 17, 35, 0),
//   ("veh1688079", "Bus", 1514227, 0.0, 35, 36, 8.0, 1.25, 12, 37, 1),
//   ("veh1582182", "Car", 1423015, 10.72, 67,66, 11.2, 5.53, 11, 16, 5),
//   ("veh6128843", "Car", 5667584, 10.04, 192, 193, 11.2, 3.31, 14, 19, 6),
//   ("veh1266933", "Car", 1165420, 9.5, 47, 48, 11.19, 3.23, 11, 43, 6),
//   ("veh1048149", "Car", 951402, 10.51, 111, 140, 11.19, 3.2, 0, 16, 3)
// ).toDF("id", "type", "time", "speed", "start_vertex_id", "stop_vertex_id", "maxSpeed", "averageSpeed", "hour", "minute", "turnsCount")
// df1.show()

// prediction of vehicle type of sample data set
// if the prediction is 1.0, it's a bus; if the prediction is 0.0, it's a car
// we don't need to add feature column to data frame since model comes with pipeline
// pipeline already have VectorAssembler
val df3 = cvModelWithauROCLoaded.transform(df2)
df3.show()

val busCountByPrediction = df3.filter(col("prediction") === 1.0).count()
val carCountByPrediction = df3.filter(col("prediction") === 0.0).count()

println(s"The number of buses from prediction are $busCountByPrediction")
println(s"The number of cars from prediction are $carCountByPrediction")