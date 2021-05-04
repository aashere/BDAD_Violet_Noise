hdfs dfs -put /scratch/hls327/traces/week_10_day_0_trace.xml /user/jl11257/big_data_project/traces/raw

hdfs dfs -mkdir /user/jl11257/big_data_project/traces/demo

spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/etl_2.11-0.1.jar \
traces/raw/week_10_day_0_trace.xml \
traces/demo/fullday

