import org.apache.spark._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.Column

object VehicleSample {
    def main(args: Array[String]) {
        val spark = SparkSession.builder().appName("VehicleSample").getOrCreate
        import spark.implicits._

        val full_data_path = "/user/jl11257/big_data_project/features/vehicleclass"
        val full_data_df = spark.read.parquet(full_data_path)

        // preserve some data in current class balance for testing
        val seed = 5043
        val Array(testData, trainingData) = full_data_df.randomSplit(Array(0.1, 0.9), seed)

        // for training data, down sampling cars 10% of car data
        val Array(choosenCarData, notChoosenCarData) = (trainingData.filter($"type" === "Car")
                                                            .randomSplit(Array(0.1, 0.9), seed))        

        val sampleTrainData = trainingData.filter($"type" === "Bus").union(choosenCarData)

        testData.coalesce(1).write.mode("append").parquet("/user/jl11257/big_data_project/features/vehiclesample/witholdtest")
        sampleTrainData.coalesce(1).write.mode("append").parquet("/user/jl11257/big_data_project/features/vehiclesample/training")

        spark.stop()

    }
}