import org.apache.spark._
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.ml.tuning.CrossValidatorModel
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, VectorIndexer, VectorAssembler}
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import scala.collection.mutable.ListBuffer


object VehiclePrediction {
    def main(args: Array[String]) = {
        val spark = SparkSession.builder().appName("VehiclePrediction").getOrCreate

        val modelPath = "/user/jl11257/big_data_project/models/vehicleClassifier/randomForestFinal"
        val dataPath = "/user/jl11257/big_data_project/features/vehiclesamplenoise/witholdtest"
        //val modelPath = args(0)
        //val dataPath = args(1)

        val cvModel = CrossValidatorModel.load(modelPath)

        val evaluator = (new BinaryClassificationEvaluator()
                            .setLabelCol("label")
                            .setRawPredictionCol("prediction")
                            .setMetricName("areaUnderPR"))

        val testdf = spark.read.parquet(dataPath)
        val predictions = cvModel.transform(testdf)
        val predsAndLabels = predictions.select("prediction","label").rdd.map(row => (row(0).toString.toDouble, row(1).toString.toDouble))
        val metrics = new MulticlassMetrics(predsAndLabels)

        val busCountActual = predictions.filter(col("label") === 1.0).count()
        val busCountPred = predictions.filter(col("prediction") === 1.0).count()
        val carCountActual = predictions.filter(col("label") === 0.0).count()
        val carCountPred = predictions.filter(col("prediction") === 0.0).count()

        var result = new ListBuffer[String]()  

        result += "There were " + busCountActual + " buses and " + carCountActual + " cars in the data set provided\n"
        result += "The model predicted " + busCountPred + " buses and " + carCountPred + " cars\n"

        result += "Confusion matrix:\n" + metrics.confusionMatrix + "\n"
        result += "Area under precision-recall curve = " + evaluator.evaluate(predictions) + "\n"
        val outp = spark.sparkContext.parallelize(result)
        outp.coalesce(1).saveAsTextFile("/user/hls327/predsum")
    }
}