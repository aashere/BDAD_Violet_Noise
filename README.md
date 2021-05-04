# BDAD_Violet_Noise


/user/jl11257/big_data_project/graph

spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 16G --executor-memory 1G \
--num-executors 18 --executor-cores 2 \
--packages com.databricks:spark-xml_2.10:0.4.1 \
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/etl_2.11-0.1.jar \
traces/raw/week_10_day_0_trace.xml traces/demo/week_10_day_0_gps

hdfs dfs -put /scratch/hls327/traces/week_10_day_0_trace.xml /user/jl11257/big_data_project/traces/raw

hdfs dfs -setfacl -R -m user:hls327:rwx /user/jl11257/big_data_project/traces
hdfs dfs -setfacl -R -m user:as12366:r-x /user/jl11257/big_data_project/traces
hdfs dfs -setfacl -R -m user:yl3750:r-x /user/jl11257/big_data_project/traces
hdfs dfs -setfacl -R -m group::r-x /user/jl11257/big_data_project/traces

# Overall Structure
     
    .
    ├── Research                    
    ├── sumoDataGeneration
    ├── runscripts  
    ├── ScalaETL
    ├── NoiseGenerator
    ├── edgeWeightForecast
    └── VehicleClassification



 ## Research

 Domain research, simulation experiments, and un-packaged testing scripts for scala development are all contained here

 ## sumoDataGeneration

 Final simulation code run on the greene cluster is here

 ## runscripts

 Scripts used throughout development to manage the data and hdfs storage.  Also contains script for final run through example.

 ## Scala ETL

 Raw data processing to be used by multiple insights down stream

  ## NoiseGenerator

 Add noise on top of output data to further bury insights

 ## edgeWeightForecast

 All feature generation, model development, and graph algorithms used to forecast edge weights and recommend a shortest path

 ## VehicleClassification

 All feature generation and model development used to classify vehicle types in the simulation
