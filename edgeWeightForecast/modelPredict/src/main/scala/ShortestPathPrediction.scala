import java.io.{File, PrintWriter}

import org.apache.spark._
import org.apache.spark.graphx._
import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.ml.feature.VectorAssembler
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

object ShortestPathPrediction {
    def main(args: Array[String]): Unit = {

        // OS sensitive things
        val spark = SparkSession.builder().master("local").appName("ShortestPathPrediction").getOrCreate
        import spark.implicits._
        //val modelPath = args(0)
        //val dataPath = args(1)
        //val base_path = "/user/jl11257/big_data_project/graph/"
        val base_path = "C:\\Users\\yingl\\OneDrive\\Desktop\\Data_OLAP\\"
        val read_path = base_path + "features\\edgeregress"
        val write_path = base_path + "predictions\\edgeWeightPrediction\\"
        val model_path = base_path + "models\\edgeWeightPrediction\\GeneralizedLinearPoisson"
        val nodes_path = base_path + "graph\\nodes"
        val vertices_path = base_path + "graph\\vertices"
        val pw = new PrintWriter(new File(write_path + s"shortestPathReport.txt" ))


        // 1.load test dataset
        val df_raw = spark.read.parquet(read_path)
        val windowSpec = Window.partitionBy('edge).orderBy('interval)
        val df_pred = df_raw.withColumn("label", lead('t_0_density, 1) over windowSpec)
          .withColumn("time_of_day", col("hour_of_day") * 60 + col("minute_of_hour"))
          .withColumn("time_of_day_sin", sin(col("time_of_day")*2*math.Pi/(24*60)) * -1)

        val cols = Array("t_0_density", "t-1_delta", "t-2_delta", "t-3_delta", "time_of_day_sin")
        val assembler = new VectorAssembler().setInputCols(cols).setOutputCol("features")
        val df_feature = assembler.setHandleInvalid("skip").transform(df_pred).filter($"label".isNotNull)

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

        pw.write("Root Mean Squared Error (RMSE) on out sample test data = " + rmse + "\n")
        pw.write("Mean squared error (MSE) on out sample test data = " + mse + "\n")
        pw.write("Regression through the origin(R2) on out sample test data = " + r2 + "\n")
        pw.write("Mean absolute error (MAE) on out sample test data = " + mae + "\n")

        // 3. shortest path
        //val weights_path = args(0)
        //val edge_weight_df = spark.read.parquet(weights_path)
        findShortestPath(spark:SparkSession, nodes_path: String, vertices_path: String, pw)
        // closing everything
        pw.close
        spark.close()
    }

    def findShortestPath(spark:SparkSession, nodes_path: String, vertices_path: String, pw: PrintWriter): Unit = {
        pw.write("start to find shortest path\n")
        val node_df = spark.read.parquet(nodes_path)
        //This is used repeatedly for printing shortest path
        val edge_node_df = spark.read.parquet(vertices_path).cache

        // PLACEHOLDER FOR REAL EDGE WEIGHTS
        val randfn = udf(() => scala.math.floor(scala.math.random*100).toInt)
        spark.udf.register("randfn",randfn)
        val edge_weight_df = edge_node_df.select("edge_id").distinct.withColumn("density",randfn())

        val edge_weight_df_join_id = edge_weight_df.join(edge_node_df, "edge_id").drop("edge_id")
        val nodeRDD = node_df.rdd.map(row => (row.getLong(row.size-1), row.toSeq.slice(0,row.toSeq.size-1)))
        val edgeRDD = edge_weight_df_join_id.rdd.map(row => Edge(row.getLong(3), row.getLong(4), row.getInt(0).toDouble))
        val graph = Graph(nodeRDD, edgeRDD)

        //Source for shortest path
        // val sourceId: VertexId = args(1).toLong
        val sourceId: VertexId = 0

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

        //Sink for shortest path
        // val sinkId: VertexId = args(2).toLong
        val sinkId: VertexId = 8

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
        println(shortestPath)
        println(agg_density)
    }
}

