import org.apache.spark.SparkContext
import org.apache.spark.ml.classification.{RandomForestClassificationModel, RandomForestClassifier}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{StandardScaler, OneHotEncoderEstimator, PCA, StringIndexer, VectorAssembler, VectorIndexer}
import org.apache.spark.sql.functions._
import org.apache.spark.sql._
import org.apache.spark.sql.types._
import java.io._

import org.apache.spark.ml.regression.{GBTRegressionModel, GBTRegressor}
import org.apache.spark.ml.stat.Correlation
import org.apache.spark.sql.expressions.Window
import org.apache.spark.ml.regression.LinearRegression

import scala.math
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.ml.regression.{RandomForestRegressionModel, RandomForestRegressor}
import org.apache.spark.ml.regression.GeneralizedLinearRegression


/*
TODO:
1. save model and apply to test data
2. print prediction
3. print where has most error
*/

object EdgeWeightPrediction {

  def main(args: Array[String]) = {

    val spark = SparkSession.builder().appName("Vehicle Classification").master("local").getOrCreate
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
    val test = df_all_data.where("rank > .1").drop("rank")

    val filteredDFs = List(0, 1, 2, 3, 4).map(m => (m, training.filter(col("interval") % 5 === m)))
    pw.write("features: " + cols.mkString(" ") + "\n")

    for ((m,filteredDF) <- filteredDFs) {
      //val m = 0
      //val filteredDF = training.filter(col("interval") % 5 === m)
      pw.write( "\n####################################################################################\n");
      pw.write( "Value of fold: " + m + "\n");
      filteredDF.cache()

      LinearRegression(spark, training, pw, m)
      GeneralizedLinearModels(spark, filteredDF, pw, m)
      RandomForestRegression(spark, filteredDF, pw, m)
      GradientBoostedTreeRegression(spark, filteredDF, pw, m)

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

  def LinearRegression(spark:SparkSession, filteredDF: DataFrame, pw: PrintWriter, m: Int): Unit = {
    pw.write("\nLinear Regression with Normal Distribution\n")

    val lr = new LinearRegression()
      .setMaxIter(10)
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
    trainingSummary.residuals.show()

    //trainingSummary.predictions.show(10)
    val df_res = trainingSummary.predictions.withColumn("Residual", col("label") - col("prediction"))
    val df_res_agg = df_res.groupBy("edge").agg(mean(abs(col("Residual"))).alias("MRSE")).sort(desc("MRSE"))
    //df_res_agg.show(10)


    val predictions = lrModel.transform(filteredDF)
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

  def GeneralizedLinearModels(spark:SparkSession, filteredDF: DataFrame, pw: PrintWriter, m: Int): Unit = {
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
    //pw.write("Deviance Residuals: ")
    //summary.residuals().show()
    // Select (prediction, true label) and compute test error.

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

    pw.write("Root Mean Squared Error (RMSE) on in sample training data = " + rmse + "\n")
    pw.write("Mean squared error (MSE) on in sample training data = " + mse + "\n")
    pw.write("Regression through the origin(R2) on in sample training data = " + r2 + "\n")
    pw.write("Mean absolute error (MAE) on in sample training data = " + mae + "\n")
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
    // Select (prediction, true label) and compute test error.

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

    pw.write("Root Mean Squared Error (RMSE) on in sample training data = " + rmse + "\n")
    pw.write("Mean squared error (MSE) on in sample training data = " + mse + "\n")
    pw.write("Regression through the origin(R2) on in sample training data = " + r2 + "\n")
    pw.write("Mean absolute error (MAE) on in sample training data = " + mae + "\n")
  }


  def RandomForestRegression(spark:SparkSession, filteredDF: DataFrame, pw: PrintWriter, m: Int): Unit = {
    pw.write("\nRandom Forest Regression\n")

    // Automatically identify categorical features, and index them.
    // Set maxCategories so features with > 4 distinct values are treated as continuous.
    val featureIndexer = new VectorIndexer()
      .setInputCol("features")
      .setOutputCol("indexedFeatures")
      .setMaxCategories(4)
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
    val predictions = model.transform(filteredDF)

    // Select example rows to display.
    predictions.select("prediction", "label", "features").show(5)

    // Select (prediction, true label) and compute test error.

    val rfModel = model.stages(1).asInstanceOf[RandomForestRegressionModel]
    //print(s"Learned regression forest model:\n ${rfModel.toDebugString}\n")
    pw.write(rfModel.featureImportances.toString+"\n")

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


  def GradientBoostedTreeRegression(spark:SparkSession, filteredDF: DataFrame, pw: PrintWriter, m: Int): Unit = {
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
      .setMaxIter(10)

    // Chain indexer and GBT in a Pipeline.
    val pipeline = new Pipeline()
      .setStages(Array(featureIndexer, gbt))

    // Train model. This also runs the indexer.
    val model = pipeline.fit(filteredDF)

    // Make predictions.
    val predictions = model.transform(filteredDF)

    // Select example rows to display.
    predictions.select("prediction", "label", "features").show(5)


    val gbtModel = model.stages(1).asInstanceOf[GBTRegressionModel]
    //print(s"Learned regression GBT model:\n ${gbtModel.toDebugString}\n")
    pw.write(gbtModel.featureImportances.toString+"\n")

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

}
