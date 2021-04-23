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

## To Regenerate Summary Stats
1. Decide for which number of days you want to generate this data. Update ```trace_file_path``` and ```NUM_DAYS``` in ```fullDensity.sc``` accordingly.
2. Decide what size delta you want the interval to be. Update ```DELTA_VALUE``` in ```densityStats.sc``` accordingly.
3. Move this whole directory to your Peel home or scratch directory using ```scp``` command.
4. ```cd``` into this directory, and open a new spark shell with the command:  
```spark-shell --deploy-mode client --num-executors 14 --driver-memory 15G --executor-memory 30G```. These are the parameters needed to process all 70 days of data. Configure parameters as needed, depending on the data size.
5. The ```fullDensity.sc``` script must be run before all others. Once in the shell, type ```:load fullDensity.sc```.
6. To regenerate the edge weight-related stats, type ```:load densityStats.sc```.
7. To regenerate the total trip time stats, type ```:load totalTripTime.sc```.
8. Exit the shell. The data should now be in the HDFS folders.