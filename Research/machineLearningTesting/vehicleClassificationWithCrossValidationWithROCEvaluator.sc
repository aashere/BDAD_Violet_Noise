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

// 1.Data set: vehicle Schema
val schema = StructType(
    StructField("id", StringType, nullable = true) ::
    StructField("type", StringType, nullable = true) ::
    StructField("maxSpeed", DoubleType, nullable = true) ::
    StructField("averageSpeed", DoubleType, nullable = true) ::
    StructField("hour", DoubleType, nullable = true) ::
    StructField("minute", DoubleType, nullable = true) ::
    StructField("start_vertex_id", DoubleType, nullable = true) ::
    StructField("stop_vertex_id", DoubleType, nullable = true) ::
    StructField("turnsCount", DoubleType, nullable = true) ::
    Nil
)    

// 2.Load data set: Load and parse the data file
val carFileName = "/user/jl11257/big_data_project/predictions/vehicleClassification/predictionWholeData/carDownSamping"
val busFileName = "/user/jl11257/big_data_project/predictions/vehicleClassification/predictionWholeData/bus"
// val carFileName = "/user/jl11257/big_data_project/testing/vehicleClassification/predictionDataOneDay/carDownSamping"
// val busFileName = "/user/jl11257/big_data_project/testing/vehicleClassification/predictionDataOneDay/bus"
val carInfo = spark.read.option("header",true).option("delimiter", ",").option("mode", "DROPMALFORMED").schema(schema).csv(carFileName)
val busInfo = spark.read.option("header",true).option("delimiter", ",").option("mode", "DROPMALFORMED").schema(schema).csv(busFileName)
val vehicleInfo = carInfo.unionAll(busInfo)

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

// evaluator with area under ROC
val evaluatorWithauROC = new BinaryClassificationEvaluator().setLabelCol("label").setMetricName("areaUnderROC")

// define cross validation stage to search through the parameters
// K-Fold cross validation with BinaryClassificationEvaluator of area under ROC
val cvWithauROC = new CrossValidator().setEstimator(pipeline).setEvaluator(evaluatorWithauROC).setEstimatorParamMaps(paramGrid).setNumFolds(5).setParallelism(10)

// fit will run cross validation and choose the best set of parameters
// this will take some time to run
val cvModelWithauROC = cvWithauROC.fit(pipelineTrainingDataCache)

// test cross validated model with test data
val cvPredictionDfWithauROC = cvModelWithauROC.transform(pipelineTestingDataCache)
cvPredictionDfWithauROC.show(10)

// 6.Evaluate model: 
// measure the accuracy of cross validated model with area under ROC
val cvAccuracyWithauROC = evaluatorWithauROC.evaluate(cvPredictionDfWithauROC)
println(cvAccuracyWithauROC)

// 7.save model
cvModelWithauROC.write.overwrite().save("/user/jl11257/big_data_project/modelSaving/vehicleClassification/cvModelWithauROC")