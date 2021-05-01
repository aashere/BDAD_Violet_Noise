import java.io._
import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.ml.classification.{RandomForestClassificationModel, RandomForestClassifier}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, VectorIndexer, VectorAssembler}
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.ml.tuning.{CrossValidator, ParamGridBuilder}
import org.apache.spark.ml.Pipeline
import org.apache.spark.sql._
import org.apache.spark.sql.types._



object VehicleClassification {
    def main(args: Array[String]) = {
        val spark = SparkSession.builder().appName("VehicleClassification").getOrCreate
        import spark.implicits._
        
        val seed = 5043
        val pw = new PrintWriter(new File("result.txt" ))
        val training_path = "/user/jl11257/big_data_project/features/vehiclesample/training"
        val oos_test_path = "/user/jl11257/big_data_project/features/vehiclesample/witholdtest"

        val trainSampleData = LoadModelData(spark, training_path)
        val ooSampleData = LoadModelData(spark, oos_test_path)

        val Array(trainData, testData) = trainSampleData.randomSplit(Array(0.8, 0.2), seed)
        trainData.cache

        // RANDOM FOREST
        val randomForestClassifier = (new RandomForestClassifier()
                                        .setImpurity("gini").setMaxDepth(10).setNumTrees(20)
                                        .setFeatureSubsetStrategy("auto").setSeed(seed))
        val randomForestModel = randomForestClassifier.fit(trainData)

        val rfSamplePredictions = randomForestModel.transform(testData)
        val rfOutSamplePredictions = randomForestModel.transform(ooSampleData)

        // LOGISTIC REGRESSION
        // train logistic regression model with training data set
        val logisticRegression = (new LogisticRegression()
                                    .setMaxIter(100)
                                    .setRegParam(0.02)
                                    .setElasticNetParam(0.8))
        val logisticRegressionModel = logisticRegression.fit(trainData)

        val lrSamplePredictions = logisticRegressionModel.transform(testData)
        val lrOutSamplePredictions = logisticRegressionModel.transform(ooSampleData)

        trainData.unpersist

        val evaluator = (new BinaryClassificationEvaluator()
                            .setLabelCol("label")
                            .setRawPredictionCol("prediction")
                            .setMetricName("areaUnderPR"))

        pw.write("Random Forest Classifier\n")
        //println(randomForestModel.toDebugString)
        pw.write("Random Forest feature importance\n")
        pw.write(randomForestModel.featureImportances.toString+"\n\n")

        pw.write("Area under Precision-Recall Curve in Down Sampled Data is " + evaluator.evaluate(rfSamplePredictions) + '\n')
        pw.write("Area under Precision-Recall Curve in Raw Test Data is " + evaluator.evaluate(rfOutSamplePredictions) +"\n\n\n")

        pw.write("Logistic Regression Classifier\n")
        pw.write(s"Coefficients: ${logisticRegressionModel.coefficients} Intercept: ${logisticRegressionModel.intercept}")

        pw.write("\nArea under Precision-Recall Curve in Down Sampled Data is " + evaluator.evaluate(lrSamplePredictions) + '\n')
        pw.write("Area under Precision-Recall Curve in Raw Test Data is " + evaluator.evaluate(lrOutSamplePredictions) +"\n\n\n")

        // closing everything
        pw.close
        spark.close()

    }

    def LoadModelData(spark:SparkSession, filepath: String): DataFrame = {
        val df = spark.read.option("mergeSchema", "true").parquet(filepath)
        val cast_df = df.select(df.columns.map {
                                    case column@"turnsCount" =>
                                    col(column).cast("Double").as(column)
                                    case column =>
                                    col(column)
                                }: _*)
        
        val cols = Array("maxSpeed", "averageSpeed", "turnsCount")
        val assembler = new VectorAssembler().setInputCols(cols).setOutputCol("features")
        val featureDf = assembler.transform(df)
        val indexer = new StringIndexer().setInputCol("type").setOutputCol("label")
        val labelDf = indexer.fit(featureDf).transform(featureDf)

        return labelDf
    }



// 3.Add feature column:
// columns that need to added to feature column
val cols = Array("maxSpeed", "averageSpeed", "turnsCount")

// VectorAssembler to add feature column
// input columns - cols
// feature column - features
val assembler = new VectorAssembler().setInputCols(cols).setOutputCol("features")


// 4.Add label column:
// StringIndexer define new 'label' column with 'result' column
val indexer = new StringIndexer().setInputCol("type").setOutputCol("label")

// 5. Build Random Forest model using cross validation to tune parameters
// split data set pipelineTraining and pipelineTesting, and run on the pipeline 
// pipelineTraining data set - 70%
// pipelineTesting data set - 30%
val seed = 5043
val Array(pipelineTrainingData, pipelineTestingData) = vehicleInfo.randomSplit(Array(0.7, 0.3), seed)
val pipelineTrainingDataCache = pipelineTrainingData.cache()
val pipelineTestingDataCache = pipelineTestingData.cache()

// VectorAssembler and StringIndexer are transformers
// RandomForestClassifier is the estimator
val randomForestClassifier = new RandomForestClassifier().setImpurity("gini").setMaxDepth(3).setNumTrees(20).setFeatureSubsetStrategy("auto").setSeed(seed)
val stages = Array(assembler, indexer, randomForestClassifier)

// build pipeline
val pipeline = new Pipeline().setStages(stages)

// parameters that needs to tune, we tune
//  1. max buns
//  2. max depth
//  3. impurity
val paramGrid = new ParamGridBuilder().addGrid(randomForestClassifier.maxBins, Array(25, 28, 31)).addGrid(randomForestClassifier.maxDepth, Array(4, 6, 8)).addGrid(randomForestClassifier.impurity, Array("entropy", "gini")).build()

// evaluator with area under PR
val evaluatorWithauPRC = new BinaryClassificationEvaluator().setLabelCol("label").setMetricName("areaUnderPR")

// K-Fold cross validation with BinaryClassificationEvaluator of area under PR
val cvWithauPRC = new CrossValidator().setEstimator(pipeline).setEvaluator(evaluatorWithauPRC).setEstimatorParamMaps(paramGrid).setNumFolds(5).setParallelism(10)

// fit will run cross validation and choose the best set of parameters
// this will take some time to run
val cvModelWithauPRC = cvWithauPRC.fit(pipelineTrainingDataCache)

// test cross validated model with test data
val cvPredictionDfWithauPRC = cvModelWithauPRC.transform(pipelineTestingDataCache)
cvPredictionDfWithauPRC.show(10)

// 6.Evaluate model: 
// measure the accuracy of cross validated model with area under PR
val cvAccuracyWithauPRC = evaluatorWithauPRC.evaluate(cvPredictionDfWithauPRC)
println(cvAccuracyWithauPRC)

// 7.save model
cvModelWithauPRC.write.overwrite().save("/user/jl11257/big_data_project/modelSaving/vehicleClassification/cvModelWithauPRC")

}
