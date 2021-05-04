hdfs dfs -mkdir /user/$(whoami)/violetnoisesummary

spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 2G \
--executor-memory 2G \
--num-executors 4 \
--class VehiclePrediction /home/$(whoami)/BDAD_Violet_Noise/VehicleClassification/modelPredict/vehicleprediction_2.11-0.1.jar \
/user/jl11257/big_data_project/features/vehiclesamplenoise/witholdtest \
/user/$(whoami)/violetnoisesummary/carclassify2

hdfs dfs -cat /user/$(whoami)/violetnoisesummary/carclassify2/part-00000