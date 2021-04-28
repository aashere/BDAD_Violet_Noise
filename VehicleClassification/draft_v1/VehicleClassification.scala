import org.apache.spark.SparkContext
import org.apache.spark.ml.classification.{RandomForestClassificationModel, RandomForestClassifier}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, VectorIndexer, VectorAssembler}
import org.apache.spark.sql.functions._
import org.apache.spark.sql._
import org.apache.spark.sql.types._
import java.io._
import org.apache.spark.mllib.classification.{SVMModel, SVMWithSGD}
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.ml.linalg.Vector
import org.apache.spark.ml.tuning.{CrossValidator, ParamGridBuilder}
import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.Window
import org.apache.spark.ml.feature.PCA
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS




/*
TODO:
 1. only implemented logistic regression cross validation
 2. downsampling
 */

object VehicleClassification {
  def main(args: Array[String]) = {
    val spark = SparkSession.builder().appName("Vehicle Classification").master("local").getOrCreate
    //val sqlContext = spark.sqlContext //new org.apache.spark.sql.SQLContext(sc)

    val pw = new PrintWriter(new File("result.txt" ))

    import spark.implicits._

    // 1.Data set: vehicle Schema
    // change schema according to the new data!!!
    val schema = StructType(
      StructField("week", IntegerType, nullable = true) ::
        StructField("day", IntegerType, nullable = true) ::
        StructField("id", StringType, nullable = true) ::
        StructField("type", StringType, nullable = true) ::
        StructField("start_lane", StringType, nullable = true) ::
        StructField("end_lane", StringType, nullable = true) ::
        StructField("max_speed", DoubleType, nullable = true) ::
        StructField("num_times", IntegerType, nullable = true) ::
        StructField("num_turns", IntegerType, nullable = true) ::
        StructField("avg_speed", DoubleType, nullable = true) ::
        Nil
    )

    // 2.Load data set: Load and parse the data file
    val path = "C:\\Users\\yingl\\OneDrive\\Desktop\\Data_OLAP\\test1"
    val vehicleInfo = spark.read.option("header",true).option("delimiter", ",").schema(schema).option("mode", "DROPMALFORMED").csv(path)
    //vehicleInfo.printSchema()
    //vehicleInfo.show(10)

    // 3.Add feature column:
    // columns that need to added to feature column
    val cols = Array("max_speed", "num_times", "num_turns", "avg_speed")

    // VectorAssembler to add feature column: combines a given list of columns into a single vector column
    // input columns - cols
    // feature column - features
    val assembler = new VectorAssembler().setInputCols(cols).setOutputCol("features")
    val featureDf = assembler.transform(vehicleInfo)
    //featureDf.printSchema()
    //featureDf.show(5)
    //featureDf.describe().show()

    // 4.Add label column:
    // StringIndexer define new 'label' column with 'result' column,
    // encodes a string column of labels to a column of label indices
    val indexer = new StringIndexer().setInputCol("type").setOutputCol("label")
    val labelDf = indexer.fit(featureDf).transform(featureDf)
    //labelDf.printSchema()
    labelDf.describe().show()
    labelDf.show(5)

    // feature selection
    // PCAFeature(spark, labelDf, pw)

    // hyper parameter training
    BinomialLogisticCV(spark, vehicleInfo, cols, pw)  // thres = 0, all bus; thres = 1, all car

    // model training
    LinearSVMClassifier(spark, labelDf, pw)
    //BinomialLogisticROC(spark, labelDf, pw)
    BinomialLogistic(spark, labelDf, pw)
    RandomForestClassifier(spark, labelDf, pw)
    pw.write("features: " + cols.mkString(" "))

    // closing everything
    pw.close
    spark.close()
}

  def PCAFeature(spark:SparkSession, labelDf: DataFrame, pw: PrintWriter): Unit = {
    val pca = new PCA()
      .setInputCol("features")
      .setOutputCol("pcaFeatures")
      .setK(3)
      .fit(labelDf)

    val result = pca.transform(labelDf).select("pcaFeatures")
    result.show(false)
  }


  def LinearSVMClassifier(spark:SparkSession, labelDf: DataFrame, pw: PrintWriter): Unit = {
    pw.write("Linear SVM Classifier (highly doubt bug in code, todo)\n")

    val sc = spark.sparkContext

    // Load training data in LIBSVM format.
    labelDf.printSchema()
    val data = labelDf.rdd.map(row => LabeledPoint(
      row.getAs[Double]("label"),
    org.apache.spark.mllib.linalg.Vectors.fromML(row.getAs[org.apache.spark.ml.linalg.SparseVector]("features").toDense)
      //Vectors.dense(Array(row.getString(-2).toDouble))
      //row.getAs[org.apache.spark.ml.linalg.DenseVector]("features")
    ))
    println(data.take(5))

    val seed = 5043
    val splits = data.randomSplit(Array(0.7, 0.3), seed)
    val training = splits(0).cache()
    val test = splits(1)

    // Run training algorithm to build the model
    val numIterations = 100
    val model = SVMWithSGD.train(training, numIterations)

    // Clear the default threshold.
    model.clearThreshold()

    // Compute raw scores on the test set.
    val scoreAndLabels = test.map { point =>
      val score = model.predict(point.features)
      (score, point.label)
    }

    // Get evaluation metrics.
    val trainErr = scoreAndLabels.filter(r => r._1 != r._2).count.toDouble / training.count
    pw.write("Training Error = " + trainErr)

    val metrics = new BinaryClassificationMetrics(scoreAndLabels)
    val auROC = metrics.areaUnderROC()

    pw.write(s"Area under ROC = $auROC"+ "\n\n")

    // Save and load model
    //model.save(sc, "target/tmp/scalaSVMWithSGDModel")
    //val sameModel = SVMModel.load(sc, "target/tmp/scalaSVMWithSGDModel")
  }

  def RandomForestClassifier(spark:SparkSession, labelDf: DataFrame, pw: PrintWriter): Unit = {
      pw.write("Random Forest Classifier\n")
    // 5. Build Random Forest model:
    // split data set training and test
    // training data set - 70%
    // test data set - 30%
    val seed = 5043
    val Array(trainingData, testData) = labelDf.randomSplit(Array(0.7, 0.3), seed)
    val trainingDataCache = trainingData.cache()
    val testDataCache = testData.cache()

    // train Random Forest model with training data set
    val randomForestClassifier = new RandomForestClassifier().setImpurity("gini").setMaxDepth(10).setNumTrees(100)
      .setFeatureSubsetStrategy("auto").setSeed(seed)
    val randomForestModel = randomForestClassifier.fit(trainingDataCache)
    println(randomForestModel.toDebugString)

    val predictionDf = randomForestModel.transform(testDataCache)
    predictionDf.show(10)

    // 6.Evaluate model:

    // measure the accuracy
    val evaluator = new BinaryClassificationEvaluator().setLabelCol("label").setMetricName("areaUnderROC")
    val accuracy = evaluator.evaluate(predictionDf)
    pw.write("Accuracy is " + accuracy + '\n')

    // evaluate model with area under ROC
    val auROC = evaluator.evaluate(predictionDf)
    pw.write("Area under ROC is " + auROC + '\n')

    // 7.feature important
    //println(randomForestModel.featureImportances)
    pw.write("Random Forest feature importance\n")
    pw.write(randomForestModel.featureImportances.toString+"\n\n")
  }

  def BinomialLogistic(spark:SparkSession, labelDf: DataFrame, pw: PrintWriter): Unit = {
    // https://medium.com/rahasak/logistic-regression-with-apache-spark-b7ec4c98cfcd
    pw.write("Binomial Logistic Classifier (TODO: maybe need down sampling for this method)\n")
    // 5. Build Random Forest model:
    // split data set training and test
    // training data set - 70%
    // test data set - 30%
    val seed = 5043
    val Array(trainingData, testData) = labelDf.randomSplit(Array(0.7, 0.3), seed)

    // train logistic regression model with training data set
    val logisticRegression = new LogisticRegression()
      .setMaxIter(100)
      .setRegParam(0.02)
      .setElasticNetParam(0.8)
      //.setThreshold(0)
    val logisticRegressionModel = logisticRegression.fit(trainingData)

    // run model with test data set to get predictions
    // this will add new columns rawPrediction, probability and prediction
    val predictionDf = logisticRegressionModel.transform(testData)

    import spark.implicits._
    predictionDf.filter($"type" === "Car").show(10)
    predictionDf.filter($"type" === "Bus").show(10)

    // 6.Evaluate model:
    // evaluate model with area under ROC
    val evaluator = new BinaryClassificationEvaluator()
      .setLabelCol("label")
      .setRawPredictionCol("prediction")
      .setMetricName("areaUnderROC")

    // measure the accuracy
    val accuracy = evaluator.evaluate(predictionDf)
    pw.write("Accuracy is " + accuracy + "\n\n")

  }


  def BinomialLogisticROC(spark:SparkSession, labelDf: DataFrame, pw: PrintWriter): Unit = {
    pw.write("Binomial Logistic ROC Metrics print out\n")

    val sc = spark.sparkContext

    // Load training data in LIBSVM format.
    labelDf.printSchema()
    val data = labelDf.rdd.map(row => LabeledPoint(
      row.getAs[Double]("label"),
      org.apache.spark.mllib.linalg.Vectors.fromML(row.getAs[org.apache.spark.ml.linalg.SparseVector]("features").toDense)
      //Vectors.dense(Array(row.getString(-2).toDouble))
      //row.getAs[org.apache.spark.ml.linalg.DenseVector]("features")
    ))
    println(data.take(5))

    val seed = 5043
    val splits = data.randomSplit(Array(0.7, 0.3), seed)
    val training = splits(0).cache()
    val test = splits(1)


    // Run training algorithm to build the model
    val model = new LogisticRegressionWithLBFGS()
      .setNumClasses(2)
      .run(training)

    // Clear the prediction threshold so the model will return probabilities
    model.clearThreshold

    // Compute raw scores on the test set
    val predictionAndLabels = test.map { case LabeledPoint(label, features) =>
      val prediction = model.predict(features)
      (prediction, label)
    }

    // Instantiate metrics object
    val metrics = new BinaryClassificationMetrics(predictionAndLabels)

    // Precision by threshold
    val precision = metrics.precisionByThreshold
    precision.foreach { case (t, p) =>
      println(s"Threshold: $t, Precision: $p")
    }

    /*
    // Recall by threshold
    val recall = metrics.recallByThreshold
    recall.foreach { case (t, r) =>
      println(s"Threshold: $t, Recall: $r")
    }

    // Precision-Recall Curve
    val PRC = metrics.pr

    // F-measure
    val f1Score = metrics.fMeasureByThreshold
    f1Score.foreach { case (t, f) =>
      pw.write(s"Threshold: $t, F-score: $f, Beta = 1 \n")
    }

    val beta = 0.5
    val fScore = metrics.fMeasureByThreshold(beta)
    f1Score.foreach { case (t, f) =>
      pw.write(s"Threshold: $t, F-score: $f, Beta = 0.5 \n")
    }
    */

    // AUPRC
    val auPRC = metrics.areaUnderPR
    pw.write("Area under precision-recall curve = " + auPRC + "\n")

    // Compute thresholds used in ROC and PR curves
    val thresholds = precision.map(_._1)
    pw.write("Compute thresholds used in ROC and PR curves = " + thresholds + "\n")

    // ROC Curve
    val roc = metrics.roc

    // AUROC
    val auROC = metrics.areaUnderROC
    pw.write("Area under ROC = " + auROC + "\n")
    pw.write("\n\n")
  }


  def BinomialLogisticCV(spark:SparkSession, vehicleInfo: DataFrame, cols: Array[String], pw: PrintWriter): Unit = {
    // train test time split 80% 20%, k fold cross validation is applied to train set

    pw.write("Binomial Logistic Classifier with Cross Validation\n")
    pw.write("thres = 0, all bus; thres = 1, all car. TODO: thres finally = 0.1 doesn't make sense?\n")

    val df = vehicleInfo.withColumn("rank", row_number().over(Window.partitionBy().orderBy("week","day")) / vehicleInfo.count())

    df.show(10)

    val training = df.where("rank <= .8").drop("rank")
    val test = df.where("rank > .8").drop("rank")

    val assembler = new VectorAssembler().setInputCols(cols).setOutputCol("features")
    val indexer = new StringIndexer().setInputCol("type").setOutputCol("label")

    val lr = new LogisticRegression()
      .setMaxIter(10)
    val pipeline = new Pipeline()
      .setStages(Array(assembler, indexer, lr))

    // We use a ParamGridBuilder to construct a grid of parameters to search over.
    // With 3 values for hashingTF.numFeatures and 2 values for lr.regParam,
    // this grid will have 3 x 2 = 6 parameter settings for CrossValidator to choose from.
    val paramGrid = new ParamGridBuilder()
      //.addGrid(lr.regParam, Array(0.1, 0.01))
      .addGrid(lr.threshold, Array(0.1, 0.3, 0.5, 0.7, 0.9))
      .build()

    // We now treat the Pipeline as an Estimator, wrapping it in a CrossValidator instance.
    // This will allow us to jointly choose parameters for all Pipeline stages.
    // A CrossValidator requires an Estimator, a set of Estimator ParamMaps, and an Evaluator.
    // Note that the evaluator here is a BinaryClassificationEvaluator and its default metric
    // is areaUnderROC.
    val cv = new CrossValidator()
      .setEstimator(pipeline)
      .setEvaluator(new BinaryClassificationEvaluator)
      .setEstimatorParamMaps(paramGrid)
      .setNumFolds(3)  // Use 3+ in practice
      .setParallelism(2)  // Evaluate up to 2 parameter settings in parallel

    // Run cross-validation, and choose the best set of parameters.
    val cvModel = cv.fit(training)
    val bestModel = cvModel.bestModel

    pw.write("Best Param: " + cvModel.getEstimatorParamMaps
      .zip(cvModel.avgMetrics)
      .maxBy(_._2)
      ._1)
    pw.write("\n\n")


    // Make predictions on test documents. cvModel uses the best model found (lrModel).
//    cvModel.transform(test)
//      .select("id", "type", "probability", "prediction")
//      .collect()
//      .foreach { case Row(id: String, text: String, prob: Vector, prediction: Double) =>
//        println(s"($id, $text) --> prob=$prob, prediction=$prediction")
//      }

  }
}
