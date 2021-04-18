import org.apache.spark.SparkContext
import org.apache.spark.mllib.classification.SVMWithSGD
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.{RandomForestClassificationModel, RandomForestClassifier}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, VectorIndexer}
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.sql._
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._


object VehicleClassification {
  def main(args: Array[String]) = {
    //accuracy: Double = 0.8219627495844504
    val spark = SparkSession.builder().appName("testings").master("local").getOrCreate
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
    // columns that need to be added to feature column
    val cols = Array("x", "y", "angle", "speed", "pos", "slope", "time")

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
  }

  def main_rfspeed(args: Array[String]): Unit = {
    //accuracy: Double = 0.9971702367384493
    val spark = SparkSession.builder().appName("testings").master("local").getOrCreate
    // 1.Data set: speed Schema
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
    val maxSpeed = vehicleInfo.groupBy("id", "type").agg(max("speed"))

    // 3.Add feature column:
    // columns that need to added to feature column
    val cols = Array("max(speed)")

    // VectorAssembler to add feature column
    // input columns - cols
    // feature column - features
    val assembler = new VectorAssembler().setInputCols(cols).setOutputCol("features")
    val featureDf = assembler.transform(maxSpeed)
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
    val randomForestClassifier = new RandomForestClassifier().setImpurity("gini").setMaxDepth(3).setNumTrees(20).setFeatureSubsetStrategy("auto").setSeed(seed)
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
  }

  def main_rfspeedtimeangle(args: Array[String]): Unit = {
    //accuracy: Double = 0.7146731621360997
    val spark = SparkSession.builder().appName("testings").master("local").getOrCreate
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
  }

//  def main_svmspeed(args: Array[String]): Unit = {
//    //Training Error = 0.13472137170851195
//    //accuracy Double = 0.865
//    val spark = SparkSession.builder().appName("testings").master("local").getOrCreate
//    // Load and parse the data file
//    val vehicleInfo = spark.read.option("header",true).csv("/user/jl11257/big_data_project/sampleSchema/gps_table.csv")
//    val maxSpeed = vehicleInfo.groupBy("id", "type").agg(max("speed"))
//
//    // Bus: 0, Car: 1
//    val parsedData = maxSpeed.map { r =>
//      var vehType = 0
//      if (r.getString(1) == "Car") {
//        vehType = 1
//      }
//      LabeledPoint(vehType, Vectors.dense(Array(r.getString(2).toDouble)))
//    }.rdd
//
//    // Run training algorithm to build the model
//    val numIterations = 20
//    val model = SVMWithSGD.train(parsedData, numIterations)
//
//    // Evaluate model on training examples and compute training error
//    val labelAndPreds = parsedData.map { point =>
//      val prediction = model.predict(point.features)
//      (point.label, prediction)
//    }
//    labelAndPreds.take(10)
//
//    val trainErr = labelAndPreds.filter(r => r._1 != r._2).count.toDouble / parsedData.count
//    println("Training Error = " + trainErr)
//
//    // val trainErr = labelAndPreds.filter(r => r._1 != r._2).count.toDouble / parsedData.count
//    // trainErr: Double = 0.13472137170851195
//
//    //println("Training Error = " + trainErr)  Training Error = 0.13472137170851195
//  }

}
