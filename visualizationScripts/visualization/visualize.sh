mv visualizations/delta_histogram/*.csv visualizations/delta_histogram/delta_hist.csv
mv visualizations/delta_histogram_interval/*.csv visualizations/delta_histogram/delta_hist_interval.csv
mv visualizations/edge_time_series/*.csv visualizations/edge_time_series/plot_edge.csv
mv visualizations/road_time_series/st/*.csv visualizations/road_time_series/st/plot_st.csv
mv visualizations/road_time_series/ave/*.csv visualizations/road_time_series/ave/plot_ave.csv
mv visualizations/total_trip_time/*.csv visualizations/total_trip_time/total_trip_time.csv

python visualize.py --type=delta_hist_edge --xscale=linear
python visualize.py --type=delta_hist_interval --xscale=linear
python visualize.py --type=plot_delta
python visualize.py --type=plot_edge
python visualize.py --type=plot_road
python visualize.py --type=total_trip_time

mkdir plots
mv *.png plots