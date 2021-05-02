hdfs dfs -mkdir /user/$(whoami)/violetnoisesummary

spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 2G \
--executor-memory 2G \
--num-executors 4 \
--class VehiclePrediction /home/$(whoami)/BDAD_Violet_Noise/VehicleClassification/modelPredict/vehicleprediction_2.11-0.1.jar \
/user/jl11257/big_data_project/models/vehicleClassifier/randomForestCVAUPRNoNoise \
/user/jl11257/big_data_project/features/vehicleclass/part-00000-f8c4250a-43a1-4003-aa7d-ecf08b452f25-c000.snappy.parquet \
/user/$(whoami)/violetnoisesummary/log1.txt