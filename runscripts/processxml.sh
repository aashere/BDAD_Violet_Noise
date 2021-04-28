#spark-submit --master yarn \
#--deploy-mode cluster \
#--driver-memory 16G --executor-memory 1G \
#--num-executors 18 --executor-cores 2 \
#--packages com.databricks:spark-xml_2.10:0.4.1 \
#--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_0_day_0_trace.xml traces/processed/week_0_day_0_gps &
#spark-submit --master yarn \
#--deploy-mode cluster \
#--driver-memory 16G --executor-memory 1G \
#--num-executors 18 --executor-cores 2 \
#--packages com.databricks:spark-xml_2.10:0.4.1 \
#--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_0_day_1_trace.xml traces/processed/week_0_day_1_gps &
#wait
#sleep 30s
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_0_day_2_trace.xml traces/processed/week_0_day_2_gps &
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_0_day_3_trace.xml traces/processed/week_0_day_3_gps &
wait
sleep 30s
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_0_day_4_trace.xml traces/processed/week_0_day_4_gps &
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_0_day_5_trace.xml traces/processed/week_0_day_5_gps &
wait
sleep 30s
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_0_day_6_trace.xml traces/processed/week_0_day_6_gps &
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_1_day_0_trace.xml traces/processed/week_1_day_0_gps &
wait
sleep 30s
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_1_day_1_trace.xml traces/processed/week_1_day_1_gps &
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_1_day_2_trace.xml traces/processed/week_1_day_2_gps &
wait
sleep 30s
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_1_day_3_trace.xml traces/processed/week_1_day_3_gps &
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_1_day_4_trace.xml traces/processed/week_1_day_4_gps &
wait
sleep 30s
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_1_day_5_trace.xml traces/processed/week_1_day_5_gps &
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_1_day_6_trace.xml traces/processed/week_1_day_6_gps &
wait
sleep 30s
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_2_day_0_trace.xml traces/processed/week_2_day_0_gps &
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_2_day_1_trace.xml traces/processed/week_2_day_1_gps &
wait
sleep 30s
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_2_day_2_trace.xml traces/processed/week_2_day_2_gps &
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_2_day_3_trace.xml traces/processed/week_2_day_3_gps &
wait
sleep 30s
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_2_day_4_trace.xml traces/processed/week_2_day_4_gps &
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_2_day_5_trace.xml traces/processed/week_2_day_5_gps &
wait
sleep 30s
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_2_day_6_trace.xml traces/processed/week_2_day_6_gps &
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_3_day_0_trace.xml traces/processed/week_3_day_0_gps &
wait
sleep 30s
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_3_day_1_trace.xml traces/processed/week_3_day_1_gps &
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_3_day_2_trace.xml traces/processed/week_3_day_2_gps &
wait
sleep 30s
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_3_day_3_trace.xml traces/processed/week_3_day_3_gps &
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_3_day_4_trace.xml traces/processed/week_3_day_4_gps &
wait
sleep 30s
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_3_day_5_trace.xml traces/processed/week_3_day_5_gps &
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_3_day_6_trace.xml traces/processed/week_3_day_6_gps &
wait
sleep 30s
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_4_day_0_trace.xml traces/processed/week_4_day_0_gps &
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_4_day_1_trace.xml traces/processed/week_4_day_1_gps &
wait
sleep 30s
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_4_day_2_trace.xml traces/processed/week_4_day_2_gps &
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_4_day_3_trace.xml traces/processed/week_4_day_3_gps &
wait
sleep 30s
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_4_day_4_trace.xml traces/processed/week_4_day_4_gps &
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_4_day_5_trace.xml traces/processed/week_4_day_5_gps &
wait
sleep 30s
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_4_day_6_trace.xml traces/processed/week_4_day_6_gps &
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_5_day_0_trace.xml traces/processed/week_5_day_0_gps &
wait
sleep 30s
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_5_day_1_trace.xml traces/processed/week_5_day_1_gps &
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_5_day_2_trace.xml traces/processed/week_5_day_2_gps &
wait
sleep 30s
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_5_day_3_trace.xml traces/processed/week_5_day_3_gps &
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_5_day_4_trace.xml traces/processed/week_5_day_4_gps &
wait
sleep 30s
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_5_day_5_trace.xml traces/processed/week_5_day_5_gps &
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_5_day_6_trace.xml traces/processed/week_5_day_6_gps &
wait
sleep 30s
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_6_day_0_trace.xml traces/processed/week_6_day_0_gps &
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_6_day_1_trace.xml traces/processed/week_6_day_1_gps &
wait
sleep 30s
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_6_day_2_trace.xml traces/processed/week_6_day_2_gps &
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_6_day_3_trace.xml traces/processed/week_6_day_3_gps &
wait
sleep 30s
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_6_day_4_trace.xml traces/processed/week_6_day_4_gps &
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_6_day_5_trace.xml traces/processed/week_6_day_5_gps &
wait
sleep 30s
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_6_day_6_trace.xml traces/processed/week_6_day_6_gps &
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_7_day_0_trace.xml traces/processed/week_7_day_0_gps &
wait
sleep 30s
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_7_day_1_trace.xml traces/processed/week_7_day_1_gps &
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_7_day_2_trace.xml traces/processed/week_7_day_2_gps &
wait
sleep 30s
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_7_day_3_trace.xml traces/processed/week_7_day_3_gps &
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_7_day_4_trace.xml traces/processed/week_7_day_4_gps &
wait
sleep 30s
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_7_day_5_trace.xml traces/processed/week_7_day_5_gps &
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_7_day_6_trace.xml traces/processed/week_7_day_6_gps &
wait
sleep 30s
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_8_day_0_trace.xml traces/processed/week_8_day_0_gps &
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_8_day_1_trace.xml traces/processed/week_8_day_1_gps &
wait
sleep 30s
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_8_day_2_trace.xml traces/processed/week_8_day_2_gps &
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_8_day_3_trace.xml traces/processed/week_8_day_3_gps &
wait
sleep 30s
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_8_day_4_trace.xml traces/processed/week_8_day_4_gps &
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_8_day_5_trace.xml traces/processed/week_8_day_5_gps &
wait
sleep 30s
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_8_day_6_trace.xml traces/processed/week_8_day_6_gps &
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_9_day_0_trace.xml traces/processed/week_9_day_0_gps &
wait
sleep 30s
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_9_day_1_trace.xml traces/processed/week_9_day_1_gps &
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_9_day_2_trace.xml traces/processed/week_9_day_2_gps &
wait
sleep 30s
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_9_day_3_trace.xml traces/processed/week_9_day_3_gps &
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_9_day_4_trace.xml traces/processed/week_9_day_4_gps &
wait
sleep 30s
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_9_day_5_trace.xml traces/processed/week_9_day_5_gps &
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_9_day_6_trace.xml traces/processed/week_9_day_6_gps &
wait
sleep 30s
