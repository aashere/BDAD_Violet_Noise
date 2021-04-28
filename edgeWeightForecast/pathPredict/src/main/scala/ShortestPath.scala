import org.apache.spark._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions.{col, udf}


object ShortestPath {
    def main(args: Array[String]) {
        val spark = SparkSession.builder().appName("ShortestPath").getOrCreate
        import spark.implicits._

        val nodes_path = "/user/jl11257/big_data_project/graph/nodes"
        val vertices_path = "/user/jl11257/big_data_project/graph/vertices"
        //val weights_path = args(0)
        //val edge_weight_df = spark.read.parquet(weights_path)

        val node_df = spark.read.parquet(nodes_path)
        val edge_node_df = spark.read.parquet(vertices_path)

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

        var tempId: VertexId = sinkId
        while(tempId != sourceId){
            val tempParent: VertexId = sssp.vertices.filter(v => v._1 == tempId).collect.toList(0)._2._2.asInstanceOf[Number].longValue
            val tempEdge = edge_node_df.filter(col("from_vertex_id") === tempParent && col("to_vertex_id") === tempId).collect()(0).getString(2)
            shortestPath = tempEdge + " " + shortestPath
            
            tempId = tempParent
        }
        println(shortestPath)

        spark.stop()
    }
}

