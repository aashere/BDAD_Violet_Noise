import java.io.{File, PrintWriter}

import org.apache.spark._
import org.apache.spark.graphx._
import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.ml.feature.{OneHotEncoderEstimator, StringIndexer, VectorAssembler}
import org.apache.spark.ml.linalg.Matrix
import org.apache.spark.ml.regression.{GeneralizedLinearRegression, GeneralizedLinearRegressionModel}
import org.apache.spark.ml.stat.Correlation
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions.{col, udf}
import org.apache.spark.sql.functions._
import org.apache.spark.sql._
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.types._
import org.apache.spark.ml.PipelineModel

import scala.collection.mutable.ListBuffer


object ShortestPathPrediction {
    def main(args: Array[String]): Unit = {

        // OS sensitive things
        val spark = SparkSession.builder().appName("ShortestPathPrediction").getOrCreate
        val sc = spark.sparkContext

        val base_path = "/user/jl11257/big_data_project/"      
        val model_path = base_path + "models/edgeWeightPrediction/" + args(0)
        val nodes_path = base_path + "graph/nodes"
        val vertices_path = base_path + "graph/vertices"
        val read_path = base_path + args(1) 
        val extra_feature_path = base_path + "graph/extra_graph_features"

        val week = args(2).toLong
        val day_of_week = args(3).toLong
        val hour_of_day = args(4).toLong
        val minute_of_hour = args(5).toLong

        val result_path = base_path + "results/shortestPathPrediction/" + s"week${week}_day${day_of_week}_hour${hour_of_day}_min${minute_of_hour}"

        import spark.implicits._
        var result = new ListBuffer[String]()

        // 1.load test dataset
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

        val df_all_data = df_feature.withColumn("rank", row_number().over(Window.partitionBy()
          .orderBy("interval")) / df_feature.count())
        val test = df_all_data.where("rank > .9").drop("rank")


        // 2. load model and test
        val model = GeneralizedLinearRegressionModel.load(model_path)
        val predictions = model.transform(test)
        val evaluatorRMSE = new RegressionEvaluator().setLabelCol("label").setPredictionCol("prediction").setMetricName("rmse")
        val rmse = evaluatorRMSE.evaluate(predictions)
        val evaluatorMSE = new RegressionEvaluator().setLabelCol("label").setPredictionCol("prediction").setMetricName("mse")
        val mse = evaluatorMSE.evaluate(predictions)
        val evaluatorR2 = new RegressionEvaluator().setLabelCol("label").setPredictionCol("prediction").setMetricName("r2")
        val r2 = evaluatorR2.evaluate(predictions)
        val evaluatorMAE = new RegressionEvaluator().setLabelCol("label").setPredictionCol("prediction").setMetricName("mae")
        val mae = evaluatorMAE.evaluate(predictions)

        result += "Root Mean Squared Error (RMSE) on out sample test data = " + rmse
        result += "Mean squared error (MSE) on out sample test data = " + mse
        result += "Regression through the origin(R2) on out sample test data = " + r2
        result += "Mean absolute error (MAE) on out sample test data = " + mae + "\n"


        // 3. shortest path
        val node_df = spark.read.parquet(nodes_path)
        val edge_node_df = spark.read.parquet(vertices_path).cache
        val df_pred_weight = predictions.filter(col("week") === week)
          .filter(col("day_of_week") === day_of_week)
          .filter(col("hour_of_day") === hour_of_day)
          .filter(col("minute_of_hour") === minute_of_hour)
          .select("edge", "label", "prediction")
          .withColumnRenamed("edge", "edge_id")

        println(df_pred_weight.count())  // TODO: only work if 476 lines, i.e. have prediction for ever edge

        result += "True Shortest Path \n"
        findShortestPath(spark:SparkSession, node_df, edge_node_df, df_pred_weight, result, "label")
        result += "Predicted Shortest Path \n"
        findShortestPath(spark:SparkSession, node_df, edge_node_df, df_pred_weight, result, "prediction")

        // closing everything
        val rdd = sc.parallelize(result)
        rdd.saveAsTextFile(result_path)

        spark.close()
    }

    def findShortestPath(spark:SparkSession, node_df: DataFrame, edge_node_df: DataFrame, edge_weight_df:DataFrame,
                         result: ListBuffer[String], density: String): Unit = {
        val edge_weight_df_join_id = edge_weight_df.join(edge_node_df, "edge_id").drop("edge_id")
        val nodeRDD = node_df.rdd.map(row => (row.getLong(row.size-1), row.toSeq.slice(0,row.toSeq.size-1)))
        //val edgeRDD = edge_weight_df_join_id.rdd.map(row => Edge(row.getLong(3), row.getLong(4), row.getInt(0).toDouble))
        val edgeRDD = edge_weight_df_join_id.rdd.map(row => Edge(row.getAs[Long]("from_vertex_id"),
            row.getAs[Long]("to_vertex_id"), row.getAs[Double](density)))
        val graph = Graph(nodeRDD, edgeRDD)

        //Source & sink for shortest path
        // val sourceId: VertexId = args(1).toLong
        val sourceId: VertexId = 0
        // val sinkId: VertexId = args(2).toLong
        val sinkId: VertexId = 8


        //Initial parent is -1 for all vertices
        val initParent: VertexId = -1
        //Initialize all distances to infinity except source
        val initialGraph = graph.mapVertices((id,_) =>
            if (id == sourceId) (0.0,-1) else (Double.PositiveInfinity,initParent))
        //Define pregel for shortest path
        val sssp = initialGraph.pregel((Double.PositiveInfinity,initParent))(
            //Function to receive message: vprog
            (id, attr, newAttr) => if (math.min(attr._1, newAttr._1) == attr._1) attr else newAttr,
            //Function to send message: sendMsg
            triplet => {
                if(triplet.srcAttr._1 + triplet.attr < triplet.dstAttr._1){
                    Iterator((triplet.dstId, (triplet.srcAttr._1 + triplet.attr, triplet.srcId)))
                }
                else {
                    Iterator.empty
                }
            },
            //Function to merge messages: mergeMsg
            (a, b) => if (math.min(a._1,b._1) == a._1) a else b
        )

        //Shortest path
        var shortestPath: String = ""
        //Aggregate density over shortest path
        val agg_density = sssp.vertices.filter(v => v._1 == sinkId).collect.toList(0)._2._1.asInstanceOf[Number].longValue

        var tempId: VertexId = sinkId

        while(tempId != sourceId){
            val tempParent: VertexId = sssp.vertices.filter(v => v._1 == tempId).collect.toList(0)._2._2.asInstanceOf[Number].longValue
            val tempEdge = edge_node_df.filter(col("from_vertex_id") === tempParent && col("to_vertex_id") === tempId).collect()(0).getString(2)
            shortestPath = tempEdge + " " + shortestPath

            tempId = tempParent
        }
        result += shortestPath
        result += agg_density.toString
    }
}

