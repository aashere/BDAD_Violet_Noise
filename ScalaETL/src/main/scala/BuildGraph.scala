import org.apache.spark._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD

object BuildGraph {
    def main(args: Array[String]) {
        val spark = SparkSession.builder().appName("BuildGraph").getOrCreate
        import spark.implicits._

        val node_table_path = "/user/jl11257/big_data_project/graph/node_table.csv"
        val edge_table_path = "/user/jl11257/big_data_project/graph/edge_table.csv"
        val nodes_path = "/user/jl11257/big_data_project/graph/nodes"
        val vertices_path = "/user/jl11257/big_data_project/graph/vertices"

        val node_df = (spark.read.format("csv")
			.option("header", "true")
			.load(node_table_path)
			.withColumnRenamed("id", "node_id")
			.withColumn("vertex_id", monotonically_increasing_id))

        val edge_df = (spark.read.format("csv")
                    .option("header","true")
                    .load(edge_table_path)
                    .withColumnRenamed("id", "edge_id")
                    .drop("type"))

        val edge_node_df = (edge_df.join(node_df
                        .select(node_df("node_id"), node_df("vertex_id")), edge_df.col("from") === node_df.col("node_id"))
                        .drop("node_id")
                        .withColumnRenamed("vertex_id", "from_vertex_id")
                        .join(node_df
                        .select(node_df("node_id"), node_df("vertex_id")), edge_df.col("to") === node_df.col("node_id"))
                        .drop("node_id")
                        .withColumnRenamed("vertex_id", "to_vertex_id"))

        node_df.coalesce(1).write.parquet(nodes_path)
        edge_node_df.coalesce(1).write.parquet(vertices_path)

        spark.stop()
    }
}