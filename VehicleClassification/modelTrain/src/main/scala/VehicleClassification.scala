import java.io._
import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.ml.classification.{RandomForestClassificationModel, RandomForestClassifier}
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
        val pw = new PrintWriter(new File("cv_model_result.txt"))
        val training_path = "/user/jl11257/big_data_project/features/vehiclesample/training"
        val oos_test_path = "/user/jl11257/big_data_project/features/vehiclesample/witholdtest"

        val trainSampleData = LoadData(spark, training_path)
        val ooSampleData = LoadData(spark, oos_test_path)
        trainSampleData.cache

        // Begin Pipeline
        val cols = Array("maxSpeed", "averageSpeed", "turnsCount")
        val assembler = new VectorAssembler().setInputCols(cols).setOutputCol("features")
        val indexer = new StringIndexer().setInputCol("type").setOutputCol("label")
        val randomForestClassifier = (new RandomForestClassifier()
                                        .setFeatureSubsetStrategy("auto")
                                        .setSeed(seed))
        val stages = Array(assembler, indexer, randomForestClassifier)
        val pipeline = new Pipeline().setStages(stages)

        // Set Up Training
        val paramGrid = (new ParamGridBuilder()
                            .addGrid(randomForestClassifier.maxBins, Array(25, 28, 31))
                            .addGrid(randomForestClassifier.maxDepth, Array(4, 6, 8))
                            .addGrid(randomForestClassifier.impurity, Array("entropy", "gini")).build())
        val evaluator = (new BinaryClassificationEvaluator()
                            .setLabelCol("label")
                            .setRawPredictionCol("prediction")
                            .setMetricName("areaUnderPR"))

        val cvWithauPRC = (new CrossValidator()
                            .setEstimator(pipeline)
                            .setEvaluator(evaluator)
                            .setEstimatorParamMaps(paramGrid)
                            .setNumFolds(5))

        val model = cvWithauPRC.fit(trainSampleData)

        trainSampleData.unpersist

        // Evaluate model on test data, both from downsampled train data, and from out of sample testing data
        val trainSamplePredictions = model.transform(trainSampleData)
        val outSamplePredictions = model.transform(ooSampleData)

        model.write.overwrite().save("/user/jl11257/big_data_project/models/vehicleClassifier/randomForestCVAUPRNoNoise")

        pw.write("Random Forest Classifier with Cross Validation\n")
        pw.write("Cross Validation Result Model\n")

        val bestMetric = model.avgMetrics.indexOf(model.avgMetrics.max)
        val bestMetricNum = model.avgMetrics(bestMetric)
        val bestParams = model.getEstimatorParamMaps(bestMetric)

        pw.write("Best Metric: " + bestMetricNum + "\n")
        pw.write("Best Parameters: " + bestParams + "\n")

        pw.write("Area under Precision-Recall Curve in Down Sampled Data is " + evaluator.evaluate(trainSamplePredictions) + '\n')
        pw.write("Area under Precision-Recall Curve in Raw Test Data is " + evaluator.evaluate(outSamplePredictions) +"\n")

        pw.write("Model saved under /user/jl11257/big_data_project/models/vehicleClassifier/randomForestCVAUPRNoNoise\n")

        pw.close
        spark.close
    }

    def LoadData(spark:SparkSession, filepath: String): DataFrame = {
        val df = spark.read.option("mergeSchema", "true").parquet(filepath)
        val cast_df = df.select(df.columns.map {
                                    case column@"turnsCount" =>
                                    col(column).cast("Double").as(column)
                                    case column =>
                                    col(column)
                                }: _*)
        return cast_df
    }
}
