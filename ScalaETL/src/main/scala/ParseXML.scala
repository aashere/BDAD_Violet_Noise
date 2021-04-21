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

    //val week = args(0)
    //val day = args(1)
    //println(s"parsing: week_${week}_day_${day}_trace.xml")

    // set up environment
    // System.setProperty("hadoop.home.dir", "C:\\winutils")
    val conf = new SparkConf().setAppName("Spark ETL")//.setMaster("local")//.setJars(Seq(System.getProperty("user.dir") + "/SparkETL.jar"))
    val sc = new SparkContext(conf)
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
    val spark = SparkSession.builder().appName("Spark ETL").getOrCreate
    import spark.implicits._

	val dirpath = "/user/jl11257/big_data_project/"
	val read_path = dirpath + args(0)
	val write_path = dirpath + args(1)

    //val read_path = "/user/jl11257/big_data_project/SimulationData_1/traces/"
    //val write_path = "/user/jl11257/big_data_project/SimulationData_1/traces/traces/"
    //val path = read_path + s"week_${week}_day_${day}_trace.xml"

    val df = sqlContext.read.format("com.databricks.spark.xml").option("rowTag", "timestep").load(read_path)

    val flat = df.select($"_time", explode($"vehicle")).select($"_time",$"col.*")
      .select($"_time".as("time").cast("Int"),$"_id".as("id"), $"_x".as("x"), $"_y".as("y"),
        $"_angle".as("angle").cast("Int"), $"_type".as("type"), $"_speed".as("speed"),
        $"_pos".as("pos"), $"_lane".as("lane"), $"_slope".as("slope").cast("Int")).repartition(6)
    //flat.show()

    flat.write.parquet(write_path)

    spark.stop()
    sc.stop()
  }
}
