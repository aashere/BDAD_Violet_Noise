spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 40G --executor-memory 2G \
--num-executors 40 --executor-cores 5 \
--class VehicleFeatureGen /home/hls327/BDAD_Violet_Noise/VehicleClassification/featurePipeline/vehiclefeaturegen_2.11-0.1.jar /user/jl11257/big_data_project/traces/processed/* /user/jl11257/big_data_project/features/vehicleclass