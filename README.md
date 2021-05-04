# BDAD_Violet_Noise
### Team Members
Ameya - as12366
Kristin - jl11257
Emma - yl3750
Helene - hls327

### Running the Demo

Data is designed to be simulated one day at a time, each output from the microsimulation is an ~8GB file.
To save time, several pre-processing steps of the data pipeline have already been run so that you can test on a small set of data.
Steps completed include generating a new day of data, parsing into parquet format, adding noise, and selecting a subset to test.
See the following scripts for details:
runscripts/simulateForEval.sh
runscripts/prepforEval.sh

You are testing on ~2 hours of data, a Monday morning between 7 and 9 AM.

The data is stored in `/user/jl11257/big_data_project/traces/demo/morningsample`

#### To run the demo, simply run `source runscripts/demo.sh` in your home user directory.

If you wish to re-run the demo, you may will want to run the following command first.

`hdfs dfs -rm -rf /user/$(whoami)/violetnoisesummary`

In the event of folder permission issues please contact hls327@nyu.edu.

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
