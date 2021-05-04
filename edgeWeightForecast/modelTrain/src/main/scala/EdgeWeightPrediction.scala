import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.ml.feature.{OneHotEncoderEstimator, StringIndexer, VectorAssembler, VectorIndexer}
import org.apache.spark.ml.regression._
import org.apache.spark.ml.{Pipeline, PipelineModel, Transformer}
import org.apache.spark.sql._
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._

import scala.collection.mutable.ListBuffer


object EdgeWeightPrediction {

  def main(args: Array[String]) = {

    // OS sensitive things
    val spark = SparkSession.builder().appName("EdgeWeightPrediction").getOrCreate
    val base_path = "/user/jl11257/big_data_project/"

    val input_model = args(0) 
    val read_path = base_path + args(1) 
    val model_path = base_path + "models/edgeWeightPrediction/" + input_model
    val result_path = base_path + "results/edgeWeightPrediction/" + input_model
    val extra_feature_path = base_path + "graph/extra_graph_features"

    import spark.implicits._

    var result = new ListBuffer[String]()
    result += "Training data from path " + read_path + "\n"
    // 1.load dataset
    val df_etl = spark.read.parquet(read_path)
    val df_extra_feature = spark.read.parquet(extra_feature_path)
    val df_raw = df_etl.join(df_extra_feature, df_etl.col("edge") === df_extra_feature.col("edge_id")).drop("edge_id")

    // 2. create dependent variable by shifting one
    val windowSpec = Window.partitionBy("edge").orderBy("interval")
    val df_pred = df_raw.withColumn("label", lead('t_0_density, 1) over windowSpec)
      .withColumn("time_of_day", col("hour_of_day") * 60 + col("minute_of_hour"))
      .withColumn("time_of_day_sin", sin(col("time_of_day")*2*math.Pi/(24*60)) * -1)

    // 3.Add feature column
    val borderIndexer = new StringIndexer().setInputCol("border_edge").setOutputCol("border_edge_indexed")
    var df_feature = borderIndexer.fit(df_pred).transform(df_pred)
    val borderEncoder = new OneHotEncoderEstimator().setInputCols(Array(borderIndexer.getOutputCol)).setOutputCols(Array("borderEncoded"))
    df_feature = borderEncoder.fit(df_feature).transform(df_feature)

    val twoWayIndexer = new StringIndexer().setInputCol("two_way_edge").setOutputCol("two_way_edge_indexed")
    df_feature = twoWayIndexer.fit(df_feature).transform(df_feature)
    val twoWayEncoder = new OneHotEncoderEstimator().setInputCols(Array(twoWayIndexer.getOutputCol)).setOutputCols(Array("twoWayEncoded"))
    df_feature = twoWayEncoder.fit(df_feature).transform(df_feature)

    val cols = Array("t_0_density", "t-1_delta", "t-2_delta", "t-3_delta", "time_of_day_sin", "borderEncoded", "twoWayEncoded")
    //val cols = Array("t_0_density", "t-1_delta", "time_of_day"+time_of_day_feature, "border_edge", "two_way_edge")
    val assembler = new VectorAssembler().setInputCols(cols).setOutputCol("features")
    df_feature = assembler.setHandleInvalid("skip").transform(df_feature).filter($"label".isNotNull)//.filter(col("label") < 100)
    //val df_clean = df_pred.filter(col("t-3_delta").isNotNull).filter(col("label").isNotNull).filter(col("label") < 100)
    //val df_feature = assembler.transform(df_clean)

    val df_all_data = df_feature.withColumn("rank", row_number().over(Window.partitionBy()
      .orderBy("interval")) / df_feature.count())
    val training = df_all_data.where("rank <= .9").drop("rank")
    val test = df_all_data.where("rank > .9").drop("rank")

    result += "features: " + cols.mkString(" ") + "\n"

    val model = input_model match {
      case "GeneralizedLinearGaussian" => GeneralizedLinearModelsGaussian(spark, training, test, result)
      case "GeneralizedLinearPoisson" => GeneralizedLinearModelsPoisson(spark, training, test, result)
      case "RandomForest" => RandomForestRegression(spark, training, test, result)
      case "GradientBoostedTree" => GradientBoostedTreeRegression(spark, training, test, result)
      case _ => LinearRegression(spark, training, test, result)  // default linear regression
    }

    // save model
    model.write.overwrite().save(model_path)
    result += "model save to path " + model_path + "\n"

    // closing everything
    val outp = spark.sparkContext.parallelize(result)
    outp.coalesce(1).saveAsTextFile(result_path)
    spark.close()
  }

  def LinearRegression(spark:SparkSession, training: DataFrame, test:DataFrame, result: ListBuffer[String]): LinearRegressionModel = {
    result += "\nLinear Regression with Normal Distribution\n"

    val lr = new LinearRegression()
      .setMaxIter(20)
      .setRegParam(0.3)
      .setElasticNetParam(0.8)
      .setSolver("normal")

    // Fit the model
    val lrModel = lr.fit(training)

    // Print the coefficients and intercept for linear regression
    result += s"Coefficients: ${lrModel.coefficients} Intercept: ${lrModel.intercept}\n"

    // Summarize the model over the training set and print out some metrics
    val trainingSummary = lrModel.summary
    result += s"numIterations: ${trainingSummary.totalIterations}\n"
    result += "in sample training stats: \n"
    ModelEvaluation(lrModel, training: DataFrame, result, "LinearRegression")
    result += "out sample training stats: \n"
    ModelEvaluation(lrModel, test: DataFrame, result, "LinearRegression")
    lrModel
  }

  def GeneralizedLinearModelsPoisson(spark:SparkSession, training: DataFrame, test:DataFrame, result: ListBuffer[String]): GeneralizedLinearRegressionModel = {
    result += "\nGeneralized Linear Regression with Poisson Distribution\n"

    val glr = new GeneralizedLinearRegression()
      .setFamily("Poisson")
      .setLink("identity")
      .setMaxIter(20)
      .setRegParam(0.3)

    // Fit the model
    val model = glr.fit(training)

    // Print the coefficients and intercept for generalized linear regression model
    result += s"Coefficients: ${model.coefficients}\n"
    result += s"Intercept: ${model.intercept}\n"

    // Summarize the model over the training set and print out some metrics
    val summary = model.summary

    result += s"Coefficient Standard Errors: ${summary.coefficientStandardErrors.mkString(",")}\n"
    result += s"T Values: ${summary.tValues.mkString(",")}\n"
    result += s"P Values: ${summary.pValues.mkString(",")}\n"
    result += s"Dispersion: ${summary.dispersion}\n"
    result += s"Null Deviance: ${summary.nullDeviance}\n"
    result += s"Residual Degree Of Freedom Null: ${summary.residualDegreeOfFreedomNull}\n"
    result += s"Deviance: ${summary.deviance}\n"
    result += s"Residual Degree Of Freedom: ${summary.residualDegreeOfFreedom}\n"
    result += s"AIC: ${summary.aic}\n"
    result += "in sample training stats: \n"
    ModelEvaluation(model, training: DataFrame, result, "GeneralizedLinearPoisson")
    result += "out sample training stats: \n"
    ModelEvaluation(model, test: DataFrame, result, "GeneralizedLinearPoisson")
    model
  }

  def GeneralizedLinearModelsGaussian(spark:SparkSession, training: DataFrame, test:DataFrame, result: ListBuffer[String]): GeneralizedLinearRegressionModel = {
    result += "\nGeneralized Linear Regression with Gaussian Distribution\n"

    val glr = new GeneralizedLinearRegression()
      .setFamily("gaussian")
      .setLink("identity")
      .setMaxIter(20)
      .setRegParam(0.3)

    // Fit the model
    val model = glr.fit(training)

    // Print the coefficients and intercept for generalized linear regression model
    result += s"Coefficients: ${model.coefficients}\n"
    result += s"Intercept: ${model.intercept}\n"

    // Summarize the model over the training set and print out some metrics
    val summary = model.summary

    result += s"Coefficient Standard Errors: ${summary.coefficientStandardErrors.mkString(",")}\n"
    result += s"T Values: ${summary.tValues.mkString(",")}\n"
    result += s"P Values: ${summary.pValues.mkString(",")}\n"
    result += s"Dispersion: ${summary.dispersion}\n"
    result += s"Null Deviance: ${summary.nullDeviance}\n"
    result += s"Residual Degree Of Freedom Null: ${summary.residualDegreeOfFreedomNull}\n"
    result += s"Deviance: ${summary.deviance}\n"
    result += s"Residual Degree Of Freedom: ${summary.residualDegreeOfFreedom}\n"
    result += s"AIC: ${summary.aic}\n"
    result += "in sample training stats: \n"
    ModelEvaluation(model, training: DataFrame, result, "GeneralizedLinear")
    result += "out sample training stats: \n"
    ModelEvaluation(model, test: DataFrame, result, "GeneralizedLinear")
    model
  }

  def RandomForestRegression(spark:SparkSession, training: DataFrame, test:DataFrame, result: ListBuffer[String]): PipelineModel = {
    result += "\nRandom Forest Regression\n"

    // Automatically identify categorical features, and index them.
    val featureIndexer = new VectorIndexer()
      .setInputCol("features")
      .setOutputCol("indexedFeatures")
      //.setMaxCategories(4)
      .fit(training)

    // Train a RandomForest model.
    val rf = new RandomForestRegressor()
      .setLabelCol("label")
      .setFeaturesCol("indexedFeatures")

    // Chain indexer and forest in a Pipeline.
    val pipeline = new Pipeline()
      .setStages(Array(featureIndexer, rf))

    // Train model. This also runs the indexer.
    val model = pipeline.fit(training)

    val rfModel = model.stages(1).asInstanceOf[RandomForestRegressionModel]
    result += rfModel.featureImportances.toString+"\n"
    result += "in sample training stats: \n"
    ModelEvaluation(model, training: DataFrame, result, "RandomForest")
    result += "out sample training stats: \n"
    ModelEvaluation(model, test: DataFrame, result, "RandomForest")
    model
  }

  def GradientBoostedTreeRegression(spark:SparkSession, training: DataFrame, test:DataFrame, result: ListBuffer[String]): PipelineModel = {
    result += "\nGradient Boosted Tree Regression\n"

    val featureIndexer = new VectorIndexer()
      .setInputCol("features")
      .setOutputCol("indexedFeatures")
      .fit(training)

    // Train a GBT model.
    val gbt = new GBTRegressor()
      .setLabelCol("label")
      .setFeaturesCol("indexedFeatures")
      .setMaxIter(100)

    // Chain indexer and GBT in a Pipeline.
    val pipeline = new Pipeline()
      .setStages(Array(featureIndexer, gbt))

    // Train model. This also runs the indexer.
    val model = pipeline.fit(training)
    val gbtModel = model.stages(1).asInstanceOf[GBTRegressionModel]
    result += gbtModel.featureImportances.toString+"\n"
    result += "in sample training stats: \n"
    ModelEvaluation(model, training: DataFrame, result, "GradientBoostedTree")
    result += "out sample training stats: \n"
    ModelEvaluation(model, test: DataFrame, result, "GradientBoostedTree")
    model
  }

  def ModelEvaluation(model: Transformer, filteredDF: DataFrame, result: ListBuffer[String], modelName: String): Unit = {
    val predictions = model.transform(filteredDF)

    //PREDICTION AND METRICS
    val evaluatorRMSE = new RegressionEvaluator().setLabelCol("label").setPredictionCol("prediction").setMetricName("rmse")
    val rmse = evaluatorRMSE.evaluate(predictions)

    //Mean Squared Error
    val evaluatorMSE = new RegressionEvaluator().setLabelCol("label").setPredictionCol("prediction").setMetricName("mse")
    val mse = evaluatorMSE.evaluate(predictions)

    //Regression through the origin
    val evaluatorR2 = new RegressionEvaluator().setLabelCol("label").setPredictionCol("prediction").setMetricName("r2")
    val r2 = evaluatorR2.evaluate(predictions)

    //Mean absolute error
    val evaluatorMAE = new RegressionEvaluator().setLabelCol("label").setPredictionCol("prediction").setMetricName("mae")
    val mae = evaluatorMAE.evaluate(predictions)

    result += "Root Mean Squared Error (RMSE) = " + rmse + "\n"
    result += "Mean squared error (MSE) = " + mse + "\n"
    result += "Regression through the origin(R2) = " + r2 + "\n"
    result += "Mean absolute error (MAE) = " + mae + "\n"
  }

}
