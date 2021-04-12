//accuracy: Double = 0.7146731621360997

import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.ml.classification.{RandomForestClassificationModel, RandomForestClassifier}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, VectorIndexer, VectorAssembler}
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator

import org.apache.spark.sql._
import org.apache.spark.sql.types._

// 1.Data set: vehicle Schema
val schema = StructType(
    StructField("id", StringType, nullable = true) ::
    StructField("x", DoubleType, nullable = true) ::
    StructField("y", DoubleType, nullable = true) ::
    StructField("angle", DoubleType, nullable = true) ::
    StructField("type", StringType, nullable = true) ::
    StructField("speed", DoubleType, nullable = true) ::
    StructField("pos", DoubleType, nullable = true) ::
    StructField("lane", StringType, nullable = true) ::
    StructField("slope", DoubleType, nullable = true) ::
    StructField("time", DoubleType, nullable = true) ::
    Nil
)

// 2.Load data set: Load and parse the data file
val vehicleInfo = spark.read.option("header",true).option("delimiter", ",").option("mode", "DROPMALFORMED").schema(schema).csv("/user/jl11257/big_data_project/sampleSchema/gps_table.csv")

// 3.Add feature column:
// columns that need to added to feature column
val cols = Array("angle", "speed", "time")

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

// train Random Forest model with training data set
val randomForestClassifier = new RandomForestClassifier().setImpurity("gini").setMaxDepth(10).setNumTrees(100).setFeatureSubsetStrategy("auto").setSeed(seed)
val randomForestModel = randomForestClassifier.fit(trainingData)
println(randomForestModel.toDebugString)

val predictionDf = randomForestModel.transform(testData)
predictionDf.show(10)

// 6.Evaluate model: 
// evaluate model with area under ROC
val evaluator = new BinaryClassificationEvaluator().setLabelCol("label").setMetricName("areaUnderROC")

// measure the accuracy
val accuracy = evaluator.evaluate(predictionDf)
println(accuracy)