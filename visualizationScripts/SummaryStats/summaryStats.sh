spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 20G --executor-memory 5G \
--num-executors 20 --executor-cores 5 \
--packages com.databricks:spark-csv_2.11:1.5.0 \
--class SummaryStats target/scala-2.11/summarystats_2.11-0.1.jar /user/jl11257/big_data_project/traces/processed/* 70 900