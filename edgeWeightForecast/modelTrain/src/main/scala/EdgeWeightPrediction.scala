import org.apache.spark.SparkContext
import org.apache.spark.ml.feature.{OneHotEncoderEstimator, PCA, StandardScaler, StringIndexer, VectorAssembler, VectorIndexer}
import org.apache.spark.sql.functions._
import org.apache.spark.sql._
import org.apache.spark.sql.types._
import java.io._

import org.apache.spark.ml.regression.{GBTRegressionModel, GBTRegressor, GeneralizedLinearRegression, GeneralizedLinearRegressionModel, LinearRegression, LinearRegressionModel, RandomForestRegressionModel, RandomForestRegressor}
import org.apache.spark.ml.stat.Correlation
import org.apache.spark.sql.expressions.Window

import scala.math
import org.apache.spark.ml.{Pipeline, PipelineModel, Transformer}
import org.apache.spark.ml.evaluation.RegressionEvaluator


object EdgeWeightPrediction {

  def main(args: Array[String]) = {

    val spark = SparkSession.builder().appName("Edge Weight Prediction").master("local").getOrCreate
    val sqlContext = spark.sqlContext //new org.apache.spark.sql.SQLContext(sc)
    sqlContext.clearCache()
    val time_of_day_feature = "_sin"  // "_sin"
    val pw = new PrintWriter(new File(s"report${time_of_day_feature}.txt" ))

    import spark.implicits._

    // 1.load dataset
    val schema = StructType(
      StructField("interval", IntegerType, nullable = true) ::
        StructField("edge", StringType, nullable = true) ::
        StructField("tot_car1_count", IntegerType, nullable = true) ::
        StructField("tot_car2_count", IntegerType, nullable = true) ::
        StructField("tot_car3_count", IntegerType, nullable = true) ::
        StructField("tot_bus_count", IntegerType, nullable = true) ::
        StructField("tot_vehicle_count", IntegerType, nullable = true) ::
        StructField("t_0_density", DoubleType, nullable = true) ::
        StructField("numLanes", IntegerType, nullable = true) ::
        StructField("edge_length", DoubleType, nullable = true) ::
        StructField("edge_area", DoubleType, nullable = true) ::
        StructField("from", StringType, nullable = true) ::
        StructField("to", StringType, nullable = true) ::
        StructField("t-1_delta", DoubleType, nullable = true) ::
        StructField("t-2_delta", DoubleType, nullable = true) ::
        StructField("t-3_delta", DoubleType, nullable = true) ::
        StructField("week", IntegerType, nullable = true) ::
        StructField("day_of_week", IntegerType, nullable = true) ::
        StructField("hour_of_day", IntegerType, nullable = true) ::
        StructField("minute_of_hour", IntegerType, nullable = true) ::
        Nil
    )

    val path = "C:\\Users\\yingl\\OneDrive\\Desktop\\Data_OLAP\\regression"
    val df_raw = spark.read.option("header",true).option("delimiter", ",").schema(schema).option("mode", "DROPMALFORMED").csv(path)


    // 2. create dependent variable by shifting one
    val windowSpec = Window.partitionBy('edge).orderBy('interval)
    val df_pred = df_raw.withColumn("label", lead('t_0_density, 1) over windowSpec)
      .withColumn("time_of_day", col("hour_of_day") * 60 + col("minute_of_hour"))
      .withColumn("time_of_day_sin", sin(col("time_of_day")*2*math.Pi/(24*60)) * -1)
      //.withColumn("time_of_day_sin", sin((col("hour_of_day") * 60 + col("minute_of_hour"))*2*math.Pi/(24*60)) * -1)
      //.withColumn("day_sin", sin(col("hour_of_day")*2*math.Pi/(24)) * -1)
      //.withColumn("minute_sin", sin(col("minute_of_hour")*2*math.Pi/(60)) * -1)
      //.withColumn("day_of_week_sin", sin(col("day_of_week")*2*math.Pi/(6)))

    // TODO: df_pred.describe().show()

    // 3.Add feature column:
    val cols = Array("t_0_density", "t-1_delta", "t-2_delta", "t-3_delta", "time_of_day"+time_of_day_feature)
    val assembler = new VectorAssembler().setInputCols(cols).setOutputCol("features")
    var df_feature = assembler.setHandleInvalid("skip").transform(df_pred).filter($"label".isNotNull)

    /*
    some feature encoding

    // one hot encoding for weekday
    //    val dayIndexer = new StringIndexer().setInputCol("day_of_week").setOutputCol("day_of_week_indexed")
    //    df_feature = dayIndexer.fit(df_feature).transform(df_feature)
    //    val dayEncoder = new OneHotEncoderEstimator().setInputCols(Array(dayIndexer.getOutputCol)).setOutputCols(Array("dayEncoded"))
    //    df_feature = dayEncoder.fit(df_feature).transform(df_feature)

    // standard scaler
    val scaler = new StandardScaler()
      .setInputCol("features")
      .setOutputCol("scaledFeatures")
    val scalerModel = scaler.fit(df_feature)
    df_feature = scalerModel.transform(df_feature)

    // PCA scaler
    val pca = new PCA()
      .setInputCol("features")
      .setOutputCol("pcaFeatures")
      .setK(3)
      .fit(df_feature)
    df_feature = pca.transform(df_feature)
    df_feature.show(10)
   */

    val df_all_data = df_feature.withColumn("rank", row_number().over(Window.partitionBy()
      .orderBy("interval")) / df_feature.count())
    val training = df_all_data.where("rank <= .9").drop("rank")
    val test = df_all_data.where("rank > .9").drop("rank")

    val filteredDFs = List(0, 1, 2, 3, 4).map(m => (m, training.filter(col("interval") % 5 === m)))
    pw.write("features: " + cols.mkString(" ") + "\n")

    // train model
    for ((m,filteredDF) <- filteredDFs) {
      //val m = 0
      //val filteredDF = training.filter(col("interval") % 5 === m)
      pw.write( "\n####################################################################################\n");
      pw.write( "Value of fold: " + m + "\n");
      filteredDF.cache()

      if (m == 0) {
        // test model based on fold 0
        val lrModel = LinearRegression(spark, filteredDF, pw, m)
        val glmModel = GeneralizedLinearModels(spark, filteredDF, pw, m)
        val baggingModel = RandomForestRegression(spark, filteredDF, pw, m)
        val boostingModel = GradientBoostedTreeRegression(spark, filteredDF, pw, m)

        TestModel(lrModel, test, pw, "LinearRegression")
        TestModel(glmModel, test, pw, "GeneralizedLinear")
        TestModel(baggingModel, test, pw, "RandomForest")
        TestModel(boostingModel, test, pw, "GradientBoostedTree")
      } else {
        LinearRegression(spark, filteredDF, pw, m)
        GeneralizedLinearModels(spark, filteredDF, pw, m)
        RandomForestRegression(spark, filteredDF, pw, m)
        GradientBoostedTreeRegression(spark, filteredDF, pw, m)
      }

      // test one hot encoding, minmax scaler, standard scaler, PCA --  not improving performance
      //GeneralizedLinearModelsFeature(spark, filteredDF, pw, "features", m)
      //GeneralizedLinearModelsFeature(spark, filteredDF, pw, "pcaFeatures", m)
      //GeneralizedLinearModelsFeature(spark, filteredDF, pw, "scaledFeatures", m)

    filteredDF.unpersist()
    }


    // closing everything
    pw.close
    spark.close()
}

  def LinearRegression(spark:SparkSession, filteredDF: DataFrame, pw: PrintWriter, m: Int): LinearRegressionModel = {
    pw.write("\nLinear Regression with Normal Distribution\n")

    val lr = new LinearRegression()
      .setMaxIter(20)
      .setRegParam(0.3)
      .setElasticNetParam(0.8)
      .setSolver("normal")

    // Fit the model
    val lrModel = lr.fit(filteredDF)

    // Print the coefficients and intercept for linear regression
    pw.write(s"Coefficients: ${lrModel.coefficients} Intercept: ${lrModel.intercept}\n")

    // Summarize the model over the training set and print out some metrics
    val trainingSummary = lrModel.summary
    pw.write(s"numIterations: ${trainingSummary.totalIterations}\n")
    pw.write(s"objectiveHistory: [${trainingSummary.objectiveHistory.mkString(",")}]\n")
    //trainingSummary.residuals.show()
    ModelEvaluation(lrModel, filteredDF, pw, "LinearRegression")
    lrModel
  }

  def GeneralizedLinearModels(spark:SparkSession, filteredDF: DataFrame, pw: PrintWriter, m: Int): GeneralizedLinearRegressionModel = {
    pw.write("\nGeneralized Linear Regression with Poisson Distribution\n")

    val glr = new GeneralizedLinearRegression()
      .setFamily("Poisson")
      .setLink("identity")
      .setMaxIter(20)
      .setRegParam(0.3)

    // Fit the model
    val model = glr.fit(filteredDF)

    // Print the coefficients and intercept for generalized linear regression model
    pw.write(s"Coefficients: ${model.coefficients}\n")
    pw.write(s"Intercept: ${model.intercept}\n")

    // Summarize the model over the training set and print out some metrics
    val summary = model.summary

    pw.write(s"Coefficient Standard Errors: ${summary.coefficientStandardErrors.mkString(",")}\n")
    pw.write(s"T Values: ${summary.tValues.mkString(",")}\n")
    pw.write(s"P Values: ${summary.pValues.mkString(",")}\n")
    pw.write(s"Dispersion: ${summary.dispersion}\n")
    pw.write(s"Null Deviance: ${summary.nullDeviance}\n")
    pw.write(s"Residual Degree Of Freedom Null: ${summary.residualDegreeOfFreedomNull}\n")
    pw.write(s"Deviance: ${summary.deviance}\n")
    pw.write(s"Residual Degree Of Freedom: ${summary.residualDegreeOfFreedom}\n")
    pw.write(s"AIC: ${summary.aic}\n")

    ModelEvaluation(model, filteredDF, pw, "GeneralizedLinear")
    model
  }

  def GeneralizedLinearModelsFeature(spark:SparkSession, filteredDF: DataFrame, pw: PrintWriter, features:String, m: Int): Unit = {
    pw.write(s"\nGeneralized Linear Regression with Poisson Distribution and feature ${features}\n")

    val glr = new GeneralizedLinearRegression()
      .setLabelCol("label")
      .setFeaturesCol(features)
      .setFamily("Poisson")
      .setLink("identity")
      .setMaxIter(20)
      .setRegParam(0.3)

    // Fit the model
    val model = glr.fit(filteredDF)

    // Print the coefficients and intercept for generalized linear regression model
    pw.write(s"Coefficients: ${model.coefficients}\n")
    pw.write(s"Intercept: ${model.intercept}\n")

    // Summarize the model over the training set and print out some metrics
    val summary = model.summary

    pw.write(s"Coefficient Standard Errors: ${summary.coefficientStandardErrors.mkString(",")}\n")
    pw.write(s"T Values: ${summary.tValues.mkString(",")}\n")
    pw.write(s"P Values: ${summary.pValues.mkString(",")}\n")
    pw.write(s"Dispersion: ${summary.dispersion}\n")
    pw.write(s"Null Deviance: ${summary.nullDeviance}\n")
    pw.write(s"Residual Degree Of Freedom Null: ${summary.residualDegreeOfFreedomNull}\n")
    pw.write(s"Deviance: ${summary.deviance}\n")
    pw.write(s"Residual Degree Of Freedom: ${summary.residualDegreeOfFreedom}\n")
    pw.write(s"AIC: ${summary.aic}\n")
    //pw.write("Deviance Residuals: ")
    //summary.residuals().show()
  }

  def RandomForestRegression(spark:SparkSession, filteredDF: DataFrame, pw: PrintWriter, m: Int): PipelineModel = {
    pw.write("\nRandom Forest Regression\n")

    // Automatically identify categorical features, and index them.
    // Set maxCategories so features with > 4 distinct values are treated as continuous.
    val featureIndexer = new VectorIndexer()
      .setInputCol("features")
      .setOutputCol("indexedFeatures")
      //.setMaxCategories(4)
      .fit(filteredDF)

    // Train a RandomForest model.
    val rf = new RandomForestRegressor()
      .setLabelCol("label")
      .setFeaturesCol("indexedFeatures")

    // Chain indexer and forest in a Pipeline.
    val pipeline = new Pipeline()
      .setStages(Array(featureIndexer, rf))

    // Train model. This also runs the indexer.
    val model = pipeline.fit(filteredDF)

    // Make predictions.
    // val predictions = model.transform(filteredDF)
    // Select example rows to display.
    // predictions.select("prediction", "label", "features").show(5)

    val rfModel = model.stages(1).asInstanceOf[RandomForestRegressionModel]
    //print(s"Learned regression forest model:\n ${rfModel.toDebugString}\n")
    pw.write(rfModel.featureImportances.toString+"\n")

    ModelEvaluation(model, filteredDF: DataFrame, pw: PrintWriter, "RandomForest")
    model
  }

  def GradientBoostedTreeRegression(spark:SparkSession, filteredDF: DataFrame, pw: PrintWriter, m: Int): PipelineModel = {
    pw.write("\nGradient Boosted Tree Regression\n")

    // Automatically identify categorical features, and index them.
    // Set maxCategories so features with > 4 distinct values are treated as continuous.
    val featureIndexer = new VectorIndexer()
      .setInputCol("features")
      .setOutputCol("indexedFeatures")
      .setMaxCategories(4)
      .fit(filteredDF)

    // Train a GBT model.
    val gbt = new GBTRegressor()
      .setLabelCol("label")
      .setFeaturesCol("indexedFeatures")
      .setMaxIter(40)

    // Chain indexer and GBT in a Pipeline.
    val pipeline = new Pipeline()
      .setStages(Array(featureIndexer, gbt))

    // Train model. This also runs the indexer.
    val model = pipeline.fit(filteredDF)

    // Make predictions.
    //val predictions = model.transform(filteredDF)
    // Select example rows to display.
    // predictions.select("prediction", "label", "features").show(5)

    val gbtModel = model.stages(1).asInstanceOf[GBTRegressionModel]
    pw.write(gbtModel.featureImportances.toString+"\n")

    ModelEvaluation(model, filteredDF: DataFrame, pw: PrintWriter, "GradientBoostedTree")
    model
  }

  def ModelEvaluation(model: Transformer, filteredDF: DataFrame, pw: PrintWriter, modelName: String): Unit = {
    val predictions = model.transform(filteredDF)

    predictions.show(10)
    val df_agg = predictions.withColumn("Residual", col("label") - col("prediction"))
      .groupBy("edge")
      .agg(
        mean(abs(col("Residual"))).alias("MAE"),
        sqrt(mean(pow("Residual", 2))).alias("RMSE"))
      .sort(desc("RMSE"))

    //df_agg.show(10)
    val agg_path = "C:\\Users\\yingl\\OneDrive\\Desktop\\Data_OLAP\\regressionResult\\edgeErrorAggregation\\"
    df_agg.write.format("csv").option("header", "true").mode(SaveMode.Overwrite).save(agg_path + modelName)

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

    pw.write("Root Mean Squared Error (RMSE) on in sample training data = " + rmse + "\n")
    pw.write("Mean squared error (MSE) on in sample training data = " + mse + "\n")
    pw.write("Regression through the origin(R2) on in sample training data = " + r2 + "\n")
    pw.write("Mean absolute error (MAE) on in sample training data = " + mae + "\n")
  }

  def TestModel(model: Transformer, testDF: DataFrame, pw: PrintWriter, modelName: String): Unit = {
    pw.write("\n\n out sample test data result from model " + modelName + "\n")
    val predictions = model.transform(testDF)

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

    pw.write("Root Mean Squared Error (RMSE) on out sample test data = " + rmse + "\n")
    pw.write("Mean squared error (MSE) on out sample test data = " + mse + "\n")
    pw.write("Regression through the origin(R2) on out sample test data = " + r2 + "\n")
    pw.write("Mean absolute error (MAE) on out sample test data = " + mae + "\n")

    val write_path = "C:\\Users\\yingl\\OneDrive\\Desktop\\Data_OLAP\\regressionResult\\fold0ModelTest\\"
    val df = predictions.select("interval","edge","tot_car1_count","tot_car2_count","tot_car3_count",
      "tot_bus_count","tot_vehicle_count","t_0_density","numLanes","edge_length","edge_area","from","to","t-1_delta",
      "t-2_delta","t-3_delta","week","day_of_week","hour_of_day","minute_of_hour","label","time_of_day",
      "time_of_day_sin","prediction")
    df.show(10)
    df.write.format("csv").option("header", "true").mode(SaveMode.Overwrite).save(write_path + modelName)
  }

}
