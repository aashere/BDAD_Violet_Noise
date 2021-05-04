import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.ml.classification.{RandomForestClassificationModel, RandomForestClassifier}
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, VectorIndexer, VectorAssembler, OneHotEncoderEstimator}
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.ml.tuning.{CrossValidator, ParamGridBuilder}
import org.apache.spark.ml.Pipeline
import org.apache.spark.sql._
import org.apache.spark.sql.types._
import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions._
import org.apache.spark.sql.Column
import scala.collection.mutable.ListBuffer

object VehicleClassification {
    def main(args: Array[String]) = {
        val spark = SparkSession.builder().appName("VehicleClassification").getOrCreate
        import spark.implicits._
        
        val seed = 5043
        val training_path = "/user/jl11257/big_data_project/features/vehiclesamplenoise/training"
        val oos_test_path = "/user/jl11257/big_data_project/features/vehiclesamplenoise/witholdtest"

        val trainSampleData = LoadData(spark, training_path)
        val ooSampleData = LoadData(spark, oos_test_path)
        trainSampleData.cache

        // Begin Pipeline
        val encoder = (new OneHotEncoderEstimator().setInputCols(Array("start_vertex_id","stop_vertex_id"))
                                                        .setOutputCols(Array("start_encoding","stop_encoding")))
        val cols = Array("maxSpeed", "averageSpeed", "turnsCount", "start_encoding", "stop_encoding")
        val assembler = new VectorAssembler().setInputCols(cols).setOutputCol("features")
        val indexer = new StringIndexer().setInputCol("type").setOutputCol("label")
        val randomForestClassifier = (new RandomForestClassifier()
                                        .setImpurity("gini")
                                        .setFeatureSubsetStrategy("auto")
                                        .setSeed(seed))
        val stages = Array(encoder, assembler, indexer, randomForestClassifier)
        val pipeline = new Pipeline().setStages(stages)

        // Set Up Training
        val paramGrid = (new ParamGridBuilder()
                            .addGrid(randomForestClassifier.maxBins, Array(20, 25, 30))
                            .addGrid(randomForestClassifier.maxDepth, Array(4, 6, 8, 10)).build())
        val evaluator = (new BinaryClassificationEvaluator()
                            .setLabelCol("label")
                            .setRawPredictionCol("prediction")
                            .setMetricName("areaUnderPR"))

        val cvWithauPRC = (new CrossValidator()
                            .setEstimator(pipeline)
                            .setEvaluator(evaluator)
                            .setEstimatorParamMaps(paramGrid)
                            .setNumFolds(3))

        val model = cvWithauPRC.fit(trainSampleData)

        trainSampleData.unpersist

        // Evaluate model on test data, both from downsampled train data, and from out of sample testing data
        val trainSamplePredictions = model.transform(trainSampleData)
        val outSamplePredictions = model.transform(ooSampleData)

        model.write.overwrite().save("/user/jl11257/big_data_project/models/vehicleClassifier/randomForestFinal")
        
        var result = new ListBuffer[String]()   

        result += "Random Forest Classifier with Cross Validation\n"
        result += "Cross Validation Result Model\n"

        val bestMetric = model.avgMetrics.indexOf(model.avgMetrics.max)
        val bestMetricNum = model.avgMetrics(bestMetric)
        val bestParams = model.getEstimatorParamMaps(bestMetric)

        result += "Best Metric: " + bestMetricNum + "\n"
        result += "Best Parameters: " + bestParams + "\n"

        result += "Area under Precision-Recall Curve in Down Sampled Data is " + evaluator.evaluate(trainSamplePredictions) +"\n"
        result += "Area under Precision-Recall Curve in Raw Test Data is " + evaluator.evaluate(outSamplePredictions) +"\n"
        result += "Model saved under /user/jl11257/big_data_project/models/vehicleClassifier/randomForestFinal\n"
        
        val outp = spark.sparkContext.parallelize(result)
        outp.coalesce(1).saveAsTextFile("/user/hls327/trainsum")

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
