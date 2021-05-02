# Summary Stats for Visualization
## File Locations
Our edge weight is density, which is calculated as number of vehicles (normalized to standard vehicle length unit; car1=4; car2=5; car3=7; bus=15) / edge area (edge length*number of lanes). The summary stats have been generated and reside in the following directories:
1. The overall histogram data for the density, deltas, and vehicle count (no aggregation) can be found in:
```/user/jl11257/big_data_project/visualizations/histogram/overall```  
2. The histogram data, grouped by edge and averaged for the density, deltas, and vehicle count can be found in:  
```/user/jl11257/big_data_project/visualizations/histogram/edge_avg```  
3. The histogram data, grouped by interval and averaged for the density, deltas, and vehicle count  can be found in:  
```/user/jl11257/big_data_project/visualizations/histogram/interval_avg``` 
4. The edge-level time series data (avg density, deltas, vehicle count by interval) can be found in:
```/user/jl11257/big_data_project/visualizations/edge_time_series```  
5. The avenue-level time series data (avg density, vehicle count by interval) can be found in:
```/user/jl11257/big_data_project/visualizations/road_time_series/ave```  
6. The street-level time series data (avg density, vehicle count by interval) can be found in:  
```/user/jl11257/big_data_project/visualizations/road_time_series/st```  
7. The histogram data for the total trip time can be found in:
```/user/jl11257/big_data_project/visualizations/total_trip_time```

## To Regenerate Summary Stats
Before regenerating summary stats, check if there is data in ```/user/jl11257/big_data_project/visualizations```. If so, delete the directories in this folder that need to be overwritten. Also, make sure the line endings in ```summaryStats.sh``` are in LF, not CRLF.
1. Compile by running ```sbt package``` inside the SummaryStats directory. 
2. Pass parameters to the script as follows. In the last line of ```summaryStats.sh```, the parameters after the jar file are in the order: ```<path-to-regression-feature-table> <path-to-trace-file>```.
3. Zip up SummaryStats and ```scp``` to Peel home or scratch directory, unzip, and ```cd``` into the directory.
4. Run ```chmod +x *.sh```
5. To submit the job, type ```./summaryStats.sh```.
6. Done! The data should now be in the HDFS folders.