import org.apache.spark._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.Column

// processed_data of one day
val processed_data_path = "/user/jl11257/big_data_project/testing/vehicleClassification/processedDataOneDay"
val df_processed_data = spark.read.format("csv").option("header", "true").load(processed_data_path)
// df_processed_data.show

// car data of one day
val df_type_car = df_processed_data.filter($"type" === "Car")
// df_type_car.show

// down sampling car data to 10% of car data
val seed = 5043
val Array(choosenCarData, notChoosenCarData) = df_type_car.randomSplit(Array(0.1, 0.9), seed)
// choosenCarData.show

// save car data of one day
choosenCarData.write.format("csv").option("header", "true").save("/user/jl11257/big_data_project/testing/vehicleClassification/predictionDataOneDay/carDownSamping")

// bus data of one day
val df_type_bus = df_processed_data.filter($"type" === "Bus")
// df_type_bus.show

// save bus data of one day
df_type_bus.write.format("csv").option("header", "true").save("/user/jl11257/big_data_project/testing/vehicleClassification/predictionDataOneDay/bus")

