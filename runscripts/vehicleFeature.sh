spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 40G --executor-memory 6G \
--num-executors 60 --executor-cores 5 \
--class VehicleFeatureGen /home/hls327/BDAD_Violet_Noise/VehicleClassification/featurePipeline/vehiclefeaturegen_2.11-0.1.jar /user/jl11257/big_data_project/traces/processed/* /user/jl11257/big_data_project/features/classify

hdfs dfs -setfacl -R -m user:hls327:rwx /user/jl11257/big_data_project/features/vehicleclass
hdfs dfs -setfacl -R -m user:as12366:r-x /user/jl11257/big_data_project/features/vehicleclass
hdfs dfs -setfacl -R -m user:yl3750:r-x /user/jl11257/big_data_project/features/vehicleclass
hdfs dfs -setfacl -R -m group::r-x /user/jl11257/big_data_project/features/vehicleclass