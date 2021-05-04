#!/bin/sh
printf "#################################### \nTeam Violet Noise \nTraffic Simulation \nDemo Run \n#################################### \n   -           __ \n --          ~( @\   \ \n---   _________]_[__/_>________ \n     /  ____ \ <>     |  ____  \ \n    =\_/ __ \_\_______|_/ __ \__D \n________(__)_____________(__)____ \n#################################### \n\n\n"

printf "Checking sufficient permissions..."

hdfs dfs -ls /user/jl11257/big_data_project/traces/demo/morningsample 1> /dev/null
[ $? -eq -1 ] && echo "Missing permissions for traces/demo/morningsample\n" exit 1

hdfs dfs -ls /user/jl11257/big_data_project/graph 1> /dev/null
[ $? -eq -1 ] && echo "Missing permissions for graph\n" exit 1

hdfs dfs -ls /user/jl11257/big_data_project/graph/edge_area 1> /dev/null
[ $? -eq -1 ] && echo "Missing permissions for graph/edge_area\n" exit 1

hdfs dfs -ls /user/jl11257/big_data_project/graph/extra_graph_features 1> /dev/null
[ $? -eq -1 ] && echo "Missing permissions for graph/extra_graph_features\n" exit 1

hdfs dfs -ls /user/jl11257/big_data_project/graph/vertices 1> /dev/null
[ $? -eq -1 ] && echo "Missing permissions for graph/vertices\n" exit 1

hdfs dfs -ls /user/jl11257/big_data_project/graph/nodes 1> /dev/null
[ $? -eq -1 ] && echo "Missing permissions for graph/vertices\n" exit 1

printf "\nInput data permissions are OK \n"

hdfs dfs -ls /user/jl11257/big_data_project/models/vehicleClassifier/randomForestFinal 1> /dev/null
[ $? -eq -1 ] && echo "Missing permissions for models/vehicleClassifier/randomForestFinal\n" exit 1

hdfs dfs -ls /user/jl11257/big_data_project/models/edgeWeightPrediction/GeneralizedLinearGaussian 1> /dev/null
[ $? -eq -1 ] && echo "Missing permissions for models/edgeWeightPrediction/GeneralizedLinearGaussian\n" exit 1

printf "Model permissions are OK\n"

printf "Making local hdfs directories for demo output\n"

hdfs dfs -mkdir /user/$(whoami)/violetnoisesummary

printf "Running feature calculations for the vehicle classification model, please allow 1-2 mins \n"

spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 4G --executor-memory 2G \
--num-executors 8 --executor-cores 3 \
--class VehicleFeatureGen /home/$(whoami)/BDAD_Violet_Noise/VehicleClassification/featurePipeline/vehiclefeaturegen_2.11-0.1.jar \
/user/jl11257/big_data_project/traces/demo/morningsample \
/user/$(whoami)/violetnoisesummary/vehiclefeatures \
2 &> vehiclefeaturelog.txt

printf "Vehicle feature spark log is in vehiclefeaturelog.txt\n"
hdfs dfs -du -h /user/$(whoami)/violetnoisesummary/vehiclefeatures

printf "Running feature calculations for the edge weight regression model, please allow 1-2 mins \n"

spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 4G --executor-memory 2G \
--num-executors 8 --executor-cores 3 \
--class EdgeFeatureGen /home/$(whoami)/BDAD_Violet_Noise/edgeWeightForecast/featurePipeline/edgefeaturegen_2.11-0.1.jar \
/user/jl11257/big_data_project/traces/demo/morningsample \
/user/$(whoami)/violetnoisesummary/edgefeatures \
900 2 &> edgefeaturelog.txt

printf "Edge weight feature spark log is in edgefeaturelog.txt\n"
hdfs dfs -du -h /user/$(whoami)/violetnoisesummary/edgefeatures

printf "Running car classification predictions on sample data, please allow 1-2 mins \n"
spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 2G \
--executor-memory 2G \
--num-executors 4 \
--class VehiclePrediction /home/$(whoami)/BDAD_Violet_Noise/VehicleClassification/modelPredict/vehicleprediction_2.11-0.1.jar \
/user/$(whoami)/violetnoisesummary/vehiclefeatures \
/user/$(whoami)/violetnoisesummary/carclassify &> carclassifylog.txt

printf "Car prediction spark log is in carclassifylog.txt\n"

hdfs dfs -cat /user/$(whoami)/violetnoisesummary/carclassify/part-00000

printf "\n\nRunning shortest path forecast on sample data, please allow 2-3 mins \n"

spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 8G \
--executor-memory 4G \
--num-executors 40 \
--packages com.databricks:spark-csv_2.11:1.5.0 \
--class ShortestPathPrediction edgeWeightForecast/modelPredict/shortest-path-prediction_2.11-0.1.jar \
/user/$(whoami)/violetnoisesummary/edgefeatures \
10 0 8 29 \
/user/$(whoami)/violetnoisesummary/edgeforecast &> edgeforecast.txt

printf "Edge forecast spark log is in edgeforecast.txt\n"

hdfs dfs -cat /user/$(whoami)/violetnoisesummary/edgeforecast/part-00000
printf "\n"

printf "                __ \n              .'  '. \n             | STOP | \n              '.__.' \n                || \n                || \n                || \n               \\||/// \n          ^^^^^^^^^^^^^^^ \n        Demo is complete! \n####################################\n"