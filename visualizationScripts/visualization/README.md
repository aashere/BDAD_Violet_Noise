# Visualization Generation
The code to generate visualizations is contained in ```visualize.py```. 
## Types of Visualizations
The following visualizations are supported:
1. Histograms of max, min, and average of t_0_density, t-1_delta, t-2_delta, t-3_delta, and tot_vehicle_count (log and linear scales)
2. Edge-level time series plot
3. Street-level time series plot
4. Avenue-level time series plot
5. Total trip time histogram
## visualize.py Options
The following parameters can be passed to the ```visualize.py``` script:
1. ```--type``` specifies the type of visualization. The following can be passed:
    a. ```--type=delta_hist``` for 1. in list above.
    b. ```--type=plot_edge``` for 2. in list above.
    c. ```--type=plot_road``` for 3. and 4. in list above.
    d. ```--type=total_trip_time``` for 5. in list above.
2. For ```--type=delta_hist``` the scale of the x-axis must also be passed via the ```--xscale``` parameter. The following can be passed:
    a. ```--xscale=linear``` for linear scale.
    b. ```--xscale=log``` for log scale.
## How to Run
1. Get the ```visualizations``` directory from HDFS, zip it up, ```scp``` to local, and unzip inside this directory.
2. Comment out lines in ```visualize.sh``` for the visualizations you do not want and/or edit/add more lines with calls to ```visualize.py``` that include the parameters you do want.
3. Run ```./visualize.sh```.
4. Done! Visualizations will be contained in the folder ```plots```.