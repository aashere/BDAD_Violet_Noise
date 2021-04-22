import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.ml.classification.{RandomForestClassificationModel, RandomForestClassifier}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, VectorIndexer, VectorAssembler}
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics

import org.apache.spark.sql._
import org.apache.spark.sql.types._

// 1.Data set: vehicle Schema
// change schema according to the new data!!!
val schema = StructType(
    StructField("id", StringType, nullable = true) ::
    StructField("type", StringType, nullable = true) ::
    StructField("speed", DoubleType, nullable = true) ::
    StructField("time", DoubleType, nullable = true) ::
    StructField("start_vertex_id", DoubleType, nullable = true) ::
    StructField("stop_vertex_id", DoubleType, nullable = true) ::
    StructField("maxSpeed", DoubleType, nullable = true) ::
    StructField("hour", DoubleType, nullable = true) ::
    StructField("minute", DoubleType, nullable = true) ::
    StructField("turnsCount", DoubleType, nullable = true) ::
    StructField("consecutiveZerosCount", DoubleType, nullable = true) ::
    Nil
)    

// 2.Load data set: Load and parse the data file
val vehicleInfo = spark.read.option("header",true).option("delimiter", ",").option("mode", "DROPMALFORMED").schema(schema).csv("/user/jl11257/big_data_project/onedaydata/gps")

// 3.Add feature column:
// columns that need to added to feature column
val cols = Array("start_vertex_id", "stop_vertex_id", "maxSpeed", "hour", "minute", "turnsCount", "consecutiveZerosCount")

// VectorAssembler to add feature column
// input columns - cols
// feature column - features
val assembler = new VectorAssembler().setInputCols(cols).setOutputCol("features")
val featureDf = assembler.transform(vehicleInfo)
featureDf.printSchema()

// 4.Add label column:
// StringIndexer define new 'label' column with 'result' column
val indexer = new StringIndexer().setInputCol("type").setOutputCol("label")
val labelDf = indexer.fit(featureDf).transform(featureDf)
labelDf.printSchema()

// 5. Build Random Forest model:
// split data set training and test
// training data set - 70%
// test data set - 30%
val seed = 5043
val Array(trainingData, testData) = labelDf.randomSplit(Array(0.7, 0.3), seed)
val trainingDataCache = trainingData.cache()
val testDataCache = testData.cache()

// train Random Forest model with training data set
val randomForestClassifier = new RandomForestClassifier().setImpurity("gini").setMaxDepth(10).setNumTrees(100).setFeatureSubsetStrategy("auto").setSeed(seed)
val randomForestModel = randomForestClassifier.fit(trainingDataCache)
println(randomForestModel.toDebugString)

val predictionDf = randomForestModel.transform(testDataCache)
predictionDf.show(10)

// val predictionAndLabels = predictionDf.select("prediction", "label").as[(Double, Double)].rdd
// predictionAndLabels.take(1)

// this could also work for converting df to rdd[(type1,type2,..)]
// val predictionAndLabels = predictionDf.select("prediction", "label").map{case Row(prediction: Double, label: Double) => (prediction, label)}.rdd

// 6.Evaluate model: 
// evaluate model with area under ROC
val evaluator_auROC = new BinaryClassificationEvaluator().setLabelCol("label").setMetricName("areaUnderROC")

// measure the accuracy with auROC
val auROC = evaluator_auROC.evaluate(predictionDf)
println(auROC)

// evaluate model with area under PR
val evaluator_auPRC = new BinaryClassificationEvaluator().setLabelCol("label").setMetricName("areaUnderPR")

// measure the accuracy with auPRC
val auPRC = evaluator_auPRC.evaluate(predictionDf)
println(auPRC)

// 7.feature important
println(randomForestModel.featureImportances)