import org.apache.spark._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD

val node_path = "/user/jl11257/big_data_project/sampleSchema/node_table.csv"
//val node_path = "/user/as12366/projectTest/test_node_table.csv"
val edge_path = "/user/jl11257/big_data_project/sampleSchema/edge_table.csv"
//val edge_path = "/user/as12366/projectTest/test_edge_table.csv"
val gps_path = "/user/jl11257/big_data_project/sampleSchema/gps_table.csv"

//Load node_table data into dataframe
val node_df = (spark.read.format("csv")
			.option("header", "true")
			.load(node_path)
			.withColumnRenamed("id", "node_id"))
node_df.show

//Add indices as vertex_ids
val node_df_with_index = node_df.withColumn("vertex_id", monotonically_increasing_id)
node_df_with_index.show

//Load edge_table data into dataframe
val edge_df = (spark.read.format("csv")
			.option("header","true")
			.load(edge_path)
			.withColumnRenamed("id", "edge_id"))
edge_df.show

//Add from and to vertex id's
val edge_df_join_from = (edge_df.join(node_df_with_index
				.select(node_df_with_index("node_id"), node_df_with_index("vertex_id")), edge_df.col("from") === node_df_with_index.col("node_id"))
				.drop("node_id")
				.withColumnRenamed("vertex_id", "from_vertex_id"))
edge_df_join_from.show

val edge_df_join_from_to = (edge_df_join_from.join(node_df_with_index
					.select(node_df_with_index("node_id"), node_df_with_index("vertex_id")), edge_df_join_from.col("to") === node_df_with_index.col("node_id"))
					.drop("node_id")
					.withColumnRenamed("vertex_id", "to_vertex_id"))
edge_df_join_from_to.show

//Load gps table data into dataframe
val gps_df = (spark.read.format("csv")
			.option("header","true")
			.load(gps_path))
gps_df.show

//Add start and stop columns
val gps_df_start_stop = (gps_df.withColumn("start", split(col("lane"),"(?<!g)(to)(?!n)")(0))
				.withColumn("stop", concat_ws("_", split(split(col("lane"),"(?<!g)(to)(?!n)")(1), "_")(0), split(split(col("lane"),"(?<!g)(to)(?!n)")(1), "_")(1))))
gps_df_start_stop.show

//Add start_vertex_id and stop_vertex_id columns
val gps_df_join_start = (gps_df_start_stop.join(node_df_with_index
					.select(node_df_with_index("node_id"), node_df_with_index("vertex_id")), gps_df_start_stop.col("start") === node_df_with_index("node_id"))
					.drop("node_id")
					.withColumnRenamed("vertex_id", "start_vertex_id"))
gps_df_join_start.show

val gps_df_join_start_stop = (gps_df_join_start.join(node_df_with_index
					.select(node_df_with_index("node_id"), node_df_with_index("vertex_id")), gps_df_join_start.col("stop") === node_df_with_index("node_id"))
					.drop("node_id")
					.withColumnRenamed("vertex_id", "stop_vertex_id"))
gps_df_join_start_stop.show


//Convert node dataframe to RDD: (vertex_id, (attributes))
val nodeRDD = node_df_with_index.rdd.map(row => (row.getLong(row.size-1), row.toSeq.slice(0,row.toSeq.size-1)))
//nodeRDD.collect().foreach(println)

//Convert edge dataframe to RDD: Edge(from_vertex_id, to_vertex_id, (attributes))
val edgeRDD = edge_df_join_to.rdd.map(row => Edge(row.getLong(row.size-2), row.getLong(row.size-1), row.toSeq.slice(0,row.toSeq.size-2)))
//edgeRDD.collect().foreach(println)

//Build graph
val graph = Graph(nodeRDD, edgeRDD)
//graph.vertices.take(10).foreach(tuple => println(tuple._1 + ": " + tuple._2.mkString(",")))
//graph.edges.take(10).foreach(edge => println("(" + edge.srcId + "," + edge.dstId + "): " + edge.attr.mkString(",")))

