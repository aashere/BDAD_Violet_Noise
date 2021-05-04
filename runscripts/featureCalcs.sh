spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 40G --executor-memory 15G \
--num-executors 60 --executor-cores 5 \
--conf spark.yarn.executor.memoryOverhead=2400 \
--class VehicleFeatureGen /home/$(whoami)/BDAD_Violet_Noise/VehicleClassification/featurePipeline/vehiclefeaturegen_2.11-0.1.jar \
/user/jl11257/big_data_project/traces/noised/* \
/user/jl11257/big_data_project/features/vehicleclassnoise \
6

spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 40G --executor-memory 12G \
--num-executors 50 --executor-cores 5 \
--conf spark.yarn.executor.memoryOverhead=2400 \
--class EdgeFeatureGen /home/$(whoami)/BDAD_Violet_Noise/edgeWeightForecast/featurePipeline/edgefeaturegen_2.11-0.1.jar \
/user/jl11257/big_data_project/traces/noised/* \
/user/jl11257/big_data_project/features/edgeregressnoise \
900 8

hdfs dfs -setfacl -R -m user:hls327:rwx /user/jl11257/big_data_project/features
hdfs dfs -setfacl -R -m user:as12366:r-x /user/jl11257/big_data_project/features
hdfs dfs -setfacl -R -m user:yl3750:r-x /user/jl11257/big_data_project/features
hdfs dfs -setfacl -R -m group::r-x /user/jl11257/big_data_project/features


spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 20G --executor-memory 10G \
--num-executors 40 --executor-cores 5 \
--class VehicleClassification /home/hls327/BDAD_Violet_Noise/VehicleClassification/modelTrain/vehicleclassification_2.11-0.1.jar