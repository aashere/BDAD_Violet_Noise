spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 40G --executor-memory 12G \
--num-executors 60 --executor-cores 5 \
--conf spark.yarn.executor.memoryOverhead=2400 \
--class VehicleFeatureGen /home/hls327/BDAD_Violet_Noise/VehicleClassification/featurePipeline/vehiclefeaturegen_2.11-0.1.jar /user/jl11257/big_data_project/traces/processed/* /user/jl11257/big_data_project/features/vehicleclass

hdfs dfs -setfacl -R -m user:hls327:rwx /user/jl11257/big_data_project/features/vehicleclass
hdfs dfs -setfacl -R -m user:as12366:r-x /user/jl11257/big_data_project/features/vehicleclass
hdfs dfs -setfacl -R -m user:yl3750:r-x /user/jl11257/big_data_project/features/vehicleclass
hdfs dfs -setfacl -R -m group::r-x /user/jl11257/big_data_project/features/vehicleclass

spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 40G --executor-memory 12G \
--num-executors 60 --executor-cores 5 \
--conf spark.yarn.executor.memoryOverhead=2400 \
--class EdgeFeatureGen /home/hls327/BDAD_Violet_Noise/edgeWeightForecast/featurePipeline/edgefeaturegen_2.11-0.1.jar /user/jl11257/big_data_project/traces/processed/* /user/jl11257/big_data_project/features/edgeregress 900

hdfs dfs -setfacl -R -m user:hls327:rwx /user/jl11257/big_data_project/features/edgeregress
hdfs dfs -setfacl -R -m user:as12366:r-x /user/jl11257/big_data_project/features/edgeregress
hdfs dfs -setfacl -R -m user:yl3750:r-x /user/jl11257/big_data_project/features/edgeregress
hdfs dfs -setfacl -R -m group::r-x /user/jl11257/big_data_project/features/edgeregress