import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.functions._
import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql._
import com.databricks.spark


object ParseXML {
    def main(args: Array[String]) {

        val spark = SparkSession.builder().appName("ParseXML").getOrCreate

        val sqlContext = spark.sqlContext //new org.apache.spark.sql.SQLContext(sc)

        import spark.implicits._

        val dirpath = "/user/jl11257/big_data_project/"
        val read_path = dirpath + args(0)
        val write_path = dirpath + args(1)

        val df = sqlContext.read.format("com.databricks.spark.xml").option("rowTag", "timestep").load(read_path)

        val flat = df.select($"_time", explode($"vehicle")).select($"_time",$"col.*")
          .select($"_time".as("time").cast("Int"),$"_id".as("id"), $"_x".as("x"), $"_y".as("y"),
            $"_angle".as("angle").cast("Int"), $"_type".as("type"), $"_speed".as("speed"),
            $"_pos".as("pos"), $"_lane".as("lane"), $"_slope".as("slope").cast("Int")).repartition(6)

        flat.write.parquet(write_path)

        spark.stop()
    }
}
