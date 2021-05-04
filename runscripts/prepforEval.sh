hdfs dfs -put /scratch/hls327/traces/week_10_day_0_trace.xml /user/jl11257/big_data_project/traces/raw

hdfs dfs -mkdir /user/jl11257/big_data_project/traces/demo

spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/$(whoami)/BDAD_Violet_Noise/ScalaETL/etl_2.11-0.1.jar \
traces/raw/week_10_day_0_trace.xml \
traces/demo/fullday

spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 20G --executor-memory 10G \
--num-executors 40 --executor-cores 5 \
--conf spark.yarn.executor.memoryOverhead=2400 \
--class NoiseGenerator /home/$(whoami)/BDAD_Violet_Noise/NoiseGenerator/noisegenerator_2.11-0.1.jar \
/user/jl11257/big_data_project/traces/demo/fullday \
/user/jl11257/big_data_project/traces/demo/fulldaynoised 4


# val path = "/user/jl11257/big_data_project/traces/demo/fulldaynoised"

# val df = spark.read.parquet(path)
# val hdf = (df.withColumn("hour", ($"time" % (24 * 60 * 60)) / (60 * 60))
# 			.withColumn("hour", col("hour").cast("int"))
#             .filter(col("hour") >= lit(7)).filter(col("hour") <= lit(9)))

# hdf.drop("hour").coalesce(1).write.parquet("/user/jl11257/big_data_project/traces/demo/morningsample")

