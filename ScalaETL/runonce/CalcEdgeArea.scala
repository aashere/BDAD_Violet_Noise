import org.apache.spark._
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.Column
import com.databricks.spark._
import org.apache.spark.sql.expressions.Window


object CalcEdgeArea {
    def main(args: Array[String]) {
        val spark = SparkSession.builder().appName("CalcEdgeArea").getOrCreate
        import spark.implicits._

        val node_table_path = "/user/jl11257/big_data_project/graph/node_table.csv"
        val edge_table_path = "/user/jl11257/big_data_project/graph/edge_table.csv"
        val edge_areas_path = "/user/jl11257/big_data_project/graph/edge_area"
        //Load node_table data into dataframe from csv
        val node_df = (spark.read.format("csv")
                                    .option("header", "true")
                                    .load(node_table_path)
                                    .withColumnRenamed("id", "node_id")
                                    .drop("type","latitude","longitude"))
        //Load edge_table data into dataframe from csv
        val edge_df = (spark.read.format("csv")
                                    .option("header","true")
                                    .load(edge_table_path)
                                    .withColumnRenamed("id", "edge_id")
                                    .withColumn("numLanes", expr("substring(type, 0, 1)"))
                                    .drop("type"))
        //Get edge areas
        val edge_areas_df = (edge_df.join(node_df, edge_df.col("from") === node_df.col("node_id"))
                                    .drop("node_id")
                                    .withColumnRenamed("x", "from_x")
                                    .withColumnRenamed("y", "from_y")
                                    .join(node_df, edge_df.col("to") === node_df.col("node_id"))
                                    .drop("node_id")
                                    .withColumnRenamed("x", "to_x")
                                    .withColumnRenamed("y", "to_y")
                                    .withColumn("edge_length", sqrt(pow(col("to_y")-col("from_y"),2)+pow(col("to_x")-col("from_x"),2)))
                                    .withColumn("edge_area", col("edge_length")*col("numLanes"))
                                    .drop("from","to","from_x","from_y","to_x","to_y"))

        //Write edge areas to csv for later use
        edge_areas_df.coalesce(1).write.parquet(edge_areas_path)

        spark.stop()
    }
}