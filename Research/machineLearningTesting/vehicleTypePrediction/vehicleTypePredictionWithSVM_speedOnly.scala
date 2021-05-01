//Training Error = 0.13472137170851195
//accuracy Double = 0.865

import org.apache.spark.SparkContext
import org.apache.spark.mllib.classification.SVMWithSGD
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.Vectors

// Load and parse the data file
val vehicleInfo = spark.read.option("header",true).csv("/user/jl11257/big_data_project/sampleSchema/gps_table.csv")
val maxSpeed = vehicleInfo.groupBy("id", "type").agg(max("speed"))

// Bus: 0, Car: 1
val parsedData = maxSpeed.map { r => 
  var vehType = 0
  if (r.getString(1) == "Car") {
    vehType = 1
  }
  LabeledPoint(vehType, Vectors.dense(Array(r.getString(2).toDouble))) 
}.rdd

// Run training algorithm to build the model
val numIterations = 20
val model = SVMWithSGD.train(parsedData, numIterations)

// Evaluate model on training examples and compute training error
val labelAndPreds = parsedData.map { point =>
  val prediction = model.predict(point.features)
  (point.label, prediction)
}
labelAndPreds.take(10)

val trainErr = labelAndPreds.filter(r => r._1 != r._2).count.toDouble / parsedData.count
println("Training Error = " + trainErr)



scala> val trainErr = labelAndPreds.filter(r => r._1 != r._2).count.toDouble / parsedData.count
trainErr: Double = 0.13472137170851195

scala> println("Training Error = " + trainErr)
Training Error = 0.13472137170851195