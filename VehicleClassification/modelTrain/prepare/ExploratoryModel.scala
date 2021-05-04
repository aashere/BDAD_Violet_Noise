import org.apache.spark.SparkContext
import org.apache.spark.ml.classification.{RandomForestClassificationModel, RandomForestClassifier}
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, VectorIndexer, VectorAssembler}
import org.apache.spark.sql.functions._
import org.apache.spark.sql._
import org.apache.spark.sql.types._
import java.io._
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.ml.linalg.Vector
import org.apache.spark.ml.tuning.{CrossValidator, ParamGridBuilder}
import org.apache.spark.sql.Row
import org.apache.spark.ml.classification.LinearSVC


object ExploratoryModel {
    def main(args: Array[String]) = {
        val spark = SparkSession.builder().appName("ExploratoryModel").getOrCreate
        import spark.implicits._
        
        val seed = 5043
        val pw = new PrintWriter(new File("exploratory_train_result2.txt" ))
        val training_path = "/user/jl11257/big_data_project/features/vehiclesamplenoise/training"
        val oos_test_path = "/user/jl11257/big_data_project/features/vehiclesamplenoise/witholdtest"

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

        // SVM
        val lsvc = (new LinearSVC()
                    .setMaxIter(20)
                    .setRegParam(0.1))
        val lsvcModel = lsvc.fit(trainData)

        val svmSamplePredictions = lsvcModel.transform(testData)
        val svmOutSamplePredictions = lsvcModel.transform(ooSampleData)

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

        pw.write("Linear Support Vector Machine\n")
        println(s"Coefficients: ${lsvcModel.coefficients} Intercept: ${lsvcModel.intercept}")

        pw.write("\nArea under Precision-Recall Curve in Down Sampled Data is " + evaluator.evaluate(svmSamplePredictions) + '\n')
        pw.write("Area under Precision-Recall Curve in Raw Test Data is " + evaluator.evaluate(svmOutSamplePredictions) +"\n\n\n")
        // closing everything
        pw.close
        spark.close

    }

    def LoadModelData(spark:SparkSession, filepath: String): DataFrame = {
        val df = spark.read.option("mergeSchema", "true").parquet(filepath)
        val cast_df = df.select(df.columns.map {
                                    case column@"hour" =>
                                    col(column).cast("Double").as(column)
                                    case column@"minute" =>
                                    col(column).cast("Double").as(column)
                                    case column@"start_vertex_id" =>
                                    col(column).cast("Double").as(column)
                                    case column@"stop_vertex_id" =>
                                    col(column).cast("Double").as(column)
                                    case column@"turnsCount" =>
                                    col(column).cast("Double").as(column)
                                    case column =>
                                    col(column)
                                }: _*)
        
        val cols = Array("maxSpeed", "averageSpeed", "hour", "minute", "start_vertex_id", "stop_vertex_id", "turnsCount")
        val assembler = new VectorAssembler().setInputCols(cols).setOutputCol("features")
        val featureDf = assembler.transform(df)
        val indexer = new StringIndexer().setInputCol("type").setOutputCol("label")
        val labelDf = indexer.fit(featureDf).transform(featureDf)

        return labelDf
    }
}
