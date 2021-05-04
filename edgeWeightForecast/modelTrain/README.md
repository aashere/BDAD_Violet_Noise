# BDAD_Violet_Noise
## Edge Weight Prediction

How to run jar:

spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 3G \
--num-executors 20 --executor-cores 4 \
--packages com.databricks:spark-csv_2.11:1.5.0 \
--class EdgeWeightPrediction edge-weight-prediction_2.11-0.1.jar \
GeneralizedLinearGaussian features/edgeregressnoise {resultPathHere}
 
 
Result is writen to: /user/jl11257/big_data_project/results/edgeWeightPrediction