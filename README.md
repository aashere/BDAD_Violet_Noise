# BDAD_Violet_Noise
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
