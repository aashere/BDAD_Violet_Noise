# Traffic Simulation Project

![Python](https://img.shields.io/badge/python-3670A0?style=for-the-badge&logo=python&logoColor=ffdd54) (pandas, matplotlib)

![Apache Spark](https://img.shields.io/badge/apache%20spark-3c3a3e?style=for-the-badge&logo=apachespark&logoColor=E25A1C) (Spark SQL, MLlib, GraphX)

![Scala](https://img.shields.io/badge/scala-%23DC322F.svg?style=for-the-badge&logo=scala&logoColor=white)

In this project, our team generated 70 days of noisy synthetic traffic data (550 GB) for a region of Manhattan. Our goal was to embed certain insights into the data and use Apache Spark libraries (Spark SQL, MLlib, GraphX) to recover them. Running our Spark code on a distributed Hadoop cluster, we did the following:

1) Classified vehicles as cars or buses
2) Forecasted the density of traffic on each street segment in our chosen region of Manhattan
3) Recommended a shortest path for a car to take from a start node to an end node

Our method and results can be found in the file: [BDAD Violet Noise Presentation.pdf](BDAD%20Violet%20Noise%20Presentation.pdf).

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
