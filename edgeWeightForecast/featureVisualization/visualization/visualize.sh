mv visualizations/histogram/overall/*.csv visualizations/histogram/overall/histogram_overall.csv
mv visualizations/histogram/edge_avg/*.csv visualizations/histogram/edge_avg/histogram_edge_avg.csv
mv visualizations/histogram/interval_avg/*.csv visualizations/histogram/interval_avg/histogram_interval_avg.csv
mv visualizations/edge_time_series/*.csv visualizations/edge_time_series/edge_time_series.csv
mv visualizations/road_time_series/ave/*.csv visualizations/road_time_series/ave/ave_time_series.csv
mv visualizations/road_time_series/st/*.csv visualizations/road_time_series/st/st_time_series.csv
mv visualizations/total_trip_time/*.csv visualizations/total_trip_time/total_trip_time.csv

mkdir plots
mkdir plots/histogram
python visualize.py --type=histogram_overall --xscale=linear
#python visualize.py --type=histogram_overall --xscale=log
python visualize.py --type=histogram_edge_avg --xscale=linear
#python visualize.py --type=histogram_edge_avg --xscale=log
python visualize.py --type=histogram_interval_avg --xscale=linear
#python visualize.py --type=histogram_interval_avg --xscale=log
mv *.png plots/histogram

mkdir plots/time_series
python visualize.py --type=plot_edge
python visualize.py --type=plot_road
mv *.png plots/time_series

mkdir plots/total_trip_time
python visualize.py --type=total_trip_time
mv *.png plots/total_trip_time