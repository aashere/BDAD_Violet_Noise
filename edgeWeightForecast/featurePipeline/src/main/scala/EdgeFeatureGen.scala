import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.functions._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql._



object EdgeFeatureGen {
  def main(args: Array[String]) {
    val spark = SparkSession.builder().appName("EdgeFeatureGen").getOrCreate
    spark.stop()
  }
}
