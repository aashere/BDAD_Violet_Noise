import org.apache.spark.ml.tuning.CrossValidatorModel

// load CrossValidatorModel model here 
val cvModelWithauPRLoaded = CrossValidatorModel.load("/user/jl11257/big_data_project/modelSaving/vehicleClassification/cvModelWithauPRC")

// sample data(right now, use the data from the generated data. we could change it to new generated data)
// the vehicle type of four four rows of data is Bus, and the last four rows of data is Car.
val df1 = Seq(
  ("veh94177", 27778, 0.0, 77, 76, 8.0, 1.59, 7, 42, 0),
  ("veh5626547", 5143831, 0.0, 45, 46, 8.0, 1.25, 12, 50, 1),
  ("veh94288", 63305, 0.0, 151, 152, 8.0, 1.27, 17, 35, 0),
  ("veh1688079", 1514227, 0.0, 35, 36, 8.0, 1.25, 12, 37, 1),
  ("veh1582182", 1423015, 10.72, 67,66, 11.2, 5.53, 11, 16, 5),
  ("veh6128843", 5667584, 10.04, 192, 193, 11.2, 3.31, 14, 19, 6),
  ("veh1266933", 1165420, 9.5, 47, 48, 11.19, 3.23, 11, 43, 6),
  ("veh1048149", 951402, 10.51, 111, 140, 11.19, 3.2, 0, 16, 3)
).toDF("id", "time", "speed", "start_vertex_id", "stop_vertex_id", "maxSpeed", "averageSpeed", "hour", "minute", "turnsCount")
df1.show()

// prediction of vehicle type of sample data set
// if the prediction is 1.0, it's a bus; if the prediction is 0.0, it's a car
// we don't need to add feature column to data frame since model comes with pipeline
// pipeline already have VectorAssembler
val df2 = cvModelWithauPRLoaded.transform(df1)
df2.show()