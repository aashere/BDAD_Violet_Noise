# Summary Stats for Visualization
## File Locations
Our edge weight is density, which is calculated as number of vehicles (normalized to standard vehicle length unit; car1=4; car2=5; car3=7; bus=15) / edge area (edge length*number of lanes). The summary stats have been generated and reside in the following directories:
1. The feature table for the linear regression can be found in:  
```/user/jl11257/big_data_project/features/regression```  
2. The histogram data for the deltas (max, min, avg of the current density, the 3 deltas, and the total vehicle count) can be found in:
```/user/jl11257/big_data_project/visualizations/delta_histogram```  
3. The edge-level time series data can be found in:
```/user/jl11257/big_data_project/visualizations/edge_time_series```  
4. The avenue-level time series data can be found in:
```/user/jl11257/big_data_project/visualizations/road_time_series/ave```  
5. The street-level time series data can be found in:  
```/user/jl11257/big_data_project/visualizations/road_time_series/st```  
6. The histogram data for the total trip time can be found in:
```/user/jl11257/big_data_project/visualizations/total_trip_time```
7. The data for delta histogram and time series by interval can be found in:
```/user/jl11257/big_data_project/visualizations/delta_histogram_interval``` 

## To Regenerate Summary Stats
Before regenerating summary stats, check if there is data in ```/user/jl11257/big_data_project/visualizations/```. If so, delete everything inside this folder (but not the folder itself). Also check if ```/user/jl11257/big_data_project/features/regression``` exists. If so, delete this folder. 
1. Make any desired changes to ```src/main/scala/SummaryStats.scala```
2. Compile by running ```sbt package``` inside the SummaryStats directory.
3. Adjust cluster parameters as needed in ```summaryStats.sh```. 
4. Pass parameters to the script as follows. In the last line of ```summaryStats.sh```, the parameters after the jar file are in the order: ```<path-to-raw-data> <number-of-days-being-processed> <delta-size-in-seconds>```.
5. Zip up SummaryStats and ```scp``` to Peel home or scratch directory, unzip, and ```cd``` into the directory.
6. Run ```chmod +x *.sh```
7. To submit the job, type ```./summaryStats.sh```.
8. Done! The data should now be in the HDFS folders.

## Job Stats
Here are job stats for the job executed on the full dataset.
### Processing Time
The job took 8 minutes in total to execute.
### Data Size
1. The linear regression feature table takes up 424 MB of disk space.
2. The deltas histogram table takes up 123.7 KB of disk space.
3. The edge-level time series table takes up 271.2 KB of disk space.
4. The avenue-level time series table takes up 1.7 MB of disk space.
5. The street-level time series table takes up 3.9 MB of disk space.
6. The total trip time table takes up 93 MB of disk space.