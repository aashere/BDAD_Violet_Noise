spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 20G --executor-memory 5G \
--num-executors 20 --executor-cores 5 \
--class VehicleFeatureGen /home/hls327/BDAD_Violet_Noise/VehicleClassification/featurePipeline/vehiclefeaturegen_2.11-0.1.jar /user/jl11257/big_data_project/traces/processed/week_7_day_5_gps /user/jl11257/big_data_project/testing/carbusv1