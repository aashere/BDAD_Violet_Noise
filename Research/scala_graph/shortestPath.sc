import org.apache.spark._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD

//val node_path = "/user/jl11257/big_data_project/sampleSchema/node_table.csv"
val node_path = "/user/as12366/projectTest/test_node_table.csv"
//val edge_path = "/user/jl11257/big_data_project/sampleSchema/edge_table.csv"
val edge_path = "/user/as12366/projectTest/test_edge_table.csv"
val shortest_path_write_path = "/user/as12366/projectTest/shortestPaths"

//Load node_table data into dataframe
val node_df = (spark.read.format("csv")
			.option("header", "true")
			.load(node_path)
			.withColumnRenamed("id", "node_id"))
//node_df.show

//Add indices as vertex_ids
val node_df_with_index = node_df.withColumn("vertex_id", monotonically_increasing_id)
//node_df_with_index.show

//Load edge_table data into dataframe
val edge_df = (spark.read.format("csv")
			.option("header","true")
			.load(edge_path)
			.withColumnRenamed("id", "edge_id"))
//edge_df.show

//Add from and to vertex id's
val edge_df_join_from = (edge_df.join(node_df_with_index
				.select(node_df_with_index("node_id"), node_df_with_index("vertex_id")), edge_df.col("from") === node_df_with_index.col("node_id"))
				.drop("node_id")
				.withColumnRenamed("vertex_id", "from_vertex_id"))
//edge_df_join_from.show

val edge_df_join_from_to = (edge_df_join_from.join(node_df_with_index
					.select(node_df_with_index("node_id"), node_df_with_index("vertex_id")), edge_df_join_from.col("to") === node_df_with_index.col("node_id"))
					.drop("node_id")
					.withColumnRenamed("vertex_id", "to_vertex_id"))
//edge_df_join_from_to.show
//val edgeRDD = edge_df_join_from_to.rdd.map(row => Edge(row.getLong(row.size-2), row.getLong(row.size-1), row.toSeq.slice(0,row.toSeq.size-2)))

val edge_weight_path = "/user/as12366/projectTest/test_edge_weight_table.csv"

//Load edge_weight_table data into dataframe
val edge_weight_df = (spark.read.format("csv")
				.option("header", "true")
				.load(edge_weight_path)
				.withColumnRenamed("id", "edge_weight_id"))

//edge_weight_df.show

//Add from_vertex_id and to_vertex_id to the edge weight df
val edge_weight_df_join_id = (edge_weight_df.join(edge_df_join_from_to.select(	edge_df_join_from_to("edge_id"),
										edge_df_join_from_to("from_vertex_id"),
										edge_df_join_from_to("to_vertex_id")),
							edge_df_join_from_to.col("edge_id") === edge_weight_df.col("edge_weight_id"))
						.drop("edge_id", "edge_weight_id"))
//edge_weight_df_join_id.show

//Get unique timestamps in an array of rows
val timestampArr = edge_weight_df_join_id.select("timestamp").distinct.collect

//Convert edge weight dataframe to rdd
val edgeWeightRDD = edge_weight_df_join_id.rdd

//Convert node dataframe with indices to Node RDD
val nodeRDD = node_df_with_index.rdd.map(row => (row.getLong(row.size-1), row.toSeq.slice(0,row.toSeq.size-1)))

//Array to store tuples of (unique timestamp, shortest path)
val shortestPathArr: Array[Tuple2[String,String]] = new Array[Tuple2[String,String]](timestampArr.length)

var i = 0
for(i<- 0 to timestampArr.length-1){
	
	//Get Edge RDD for this timestamp
	val edgeRDD = edgeWeightRDD.filter(row => row.getString(0) == timestampArr(i).getString(0)).map(row => Edge(row.getLong(2), row.getLong(3), row.getString(1).toDouble))
	//Create Graph object for this timestamp
	val graph = Graph(nodeRDD, edgeRDD)
	//graph.vertices.collect.foreach(println) 
	//graph.edges.collect.foreach(println)

	//SHORTEST PATHS IMPLEMENTATION
	
	//Source for shortest path
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
	//println(sssp.vertices.collect.mkString("\n"))

	//Sink for shortest path
	val sinkId: VertexId = 8
	
	//Shortest path
	var shortestPath: String = ""
	
	var tempId: VertexId = sinkId
	while(tempId != sourceId){
		val tempParent: VertexId = sssp.vertices.filter(v => v._1 == tempId).collect.toList(0)._2._2.asInstanceOf[Number].longValue
		val tempEdge = edge_df_join_from_to.filter(col("from_vertex_id") === tempParent && col("to_vertex_id") === tempId).collect()(0).getString(2)
		shortestPath = tempEdge + " " + shortestPath
		
		tempId = tempParent
	}
	shortestPathArr(i) = (timestampArr(i).getString(0), shortestPath)
}
//shortestPathArr.foreach(println)
val shortestPathDF = sc.parallelize(shortestPathArr).toDF("timestamp","shortest_path")
//shortestPathDF.show
//Write shortest paths to csv file
(shortestPathDF.coalesce(1)
		.write.format("com.databricks.spark.csv")
		.option("header", "true")
		.save(shortest_path_write_path))
