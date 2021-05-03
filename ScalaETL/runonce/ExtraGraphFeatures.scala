import org.apache.spark._

object ExtraGraphFeatures {
    def main(args: Array[String]) {
        val spark = SparkSession.builder().appName("ExtraGraphFeatures").getOrCreate
        import spark.implicits._

        val edge_table_path = "/user/jl11257/big_data_project/graph/edge_table.csv"
        val write_path = "/user/jl11257/big_data_project/graph/extra_graph_features"

        val extra_features = (spark.read.format("csv")
                                        .option("header","true")
                                        .load(edge_table_path)
                                        .withColumnRenamed("id", "edge_id")
                                        .drop("type")
                                        .withColumn("from_ave", split(col("from"),"_")(0))
                                        .withColumn("from_st", split(col("from"),"_")(1))
                                        .withColumn("to_ave", split(col("to"),"_")(0))
                                        .withColumn("to_st", split(col("to"),"_")(1))
                                        .withColumn("ave", when(col("from_ave") === col("to_ave"),col("from_ave")).otherwise(""))
                                        .withColumn("st", when(col("from_st") === col("to_st"),col("from_st")).otherwise(""))
                                        .withColumn("border_edge", when(col("ave") === "9" || 
                                                                        col("ave") === "Lexington" ||
                                                                        col("st") === "30" ||
                                                                        col("st") === "58", 1)
                                                                    .otherwise(0))
                                        .withColumn("two_way_edge", when(col("ave") === "Park" || 
                                                                        col("st") === "34" ||
                                                                        col("st") === "42" ||
                                                                        col("st") === "57", 1)
                                                                    .otherwise(0))
                                        .drop("from","to","from_ave","from_st","to_ave","to_st","ave","st"))

        extra_features.coalesce(1).write.parquet(write_path)

        spark.stop()
    }
}