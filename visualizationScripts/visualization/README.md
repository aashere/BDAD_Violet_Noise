# Visualization Generation
The code to generate visualizations is contained in ```visualize.py```. 
## Types of Visualizations
The following visualizations are supported:
1. Histograms overall of: t_0_density, t-1_delta, t-2_delta, t-3_delta, and tot_vehicle_count (log and linear scales)
2. Histograms by edge of avg: t_0_density, t-1_delta, t-2_delta, t-3_delta, and tot_vehicle_count (log and linear scales)
3. Histograms by interval of avg: t_0_density, t-1_delta, t-2_delta, t-3_delta, and tot_vehicle_count (log and linear scales)
4. Edge-level time series plot of avg: t_0_density, t-1_delta, t-2_delta, t-3_delta, and tot_vehicle_count
5. Avenue-level time series plot of avg: t_0_density and tot_vehicle_count
6. Street-level time series plot of avg: t_0_density and tot_vehicle_count
7. Total trip time histogram (by vehicle)
## visualize.py Options
The following parameters can be passed to the ```visualize.py``` script:
1. ```--type``` specifies the type of visualization. The following can be passed:
    a. ```--type=histogram_overall``` for 1. in list above.
    b. ```--type=histogram_edge_avg``` for 2. in list above.
    c. ```--type=histogram_interval_avg``` for 3. in list above.
    d. ```--type=plot_edge``` for 4. in list above.
    e. ```--type=plot_road``` for 5. and 6. in list above.
    f. ```--type=total_trip_time``` for 7. in list above.
2. For the histogram visualizations, the scale of the x-axis must also be passed via the ```--xscale``` parameter. The following can be passed:
    a. ```--xscale=linear``` for linear scale.
    b. ```--xscale=log``` for log scale.
## How to Run
1. Get the ```visualizations``` directory from HDFS, zip it up, ```scp``` to local, and unzip inside this directory.
2. Comment out lines in ```visualize.sh``` for the visualizations you do not want and/or edit/add more lines with calls to ```visualize.py``` that include the parameters you do want.
3. Run ```./visualize.sh```.
4. Done! Visualizations will be contained in the folder ```plots```.