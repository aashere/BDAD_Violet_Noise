spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 2G \
--executor-memory 2G \
--num-executors 4 \
--class EdgeFeatureGen /home/$(whoami)/BDAD_Violet_Noise/VehicleClassification/modelPredict/vehicleprediction_2.11-0.1.jar \
<modelpath> <filepath>