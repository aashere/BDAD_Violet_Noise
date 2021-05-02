spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 20G --executor-memory 5G \
--num-executors 20 --executor-cores 5 \
--packages com.databricks:spark-csv_2.11:1.5.0 \
--class SummaryStats target/scala-2.11/summarystats_2.11-0.1.jar features/edgeregress/* traces/processed/*

cd ..

hdfs dfs -setfacl -R -m user:yl3750:r-x /user/jl11257/big_data_project/visualizations/histogram
hdfs dfs -setfacl -R -m user:yl3750:r-x /user/jl11257/big_data_project/visualizations/edge_time_series
hdfs dfs -setfacl -R -m user:yl3750:r-x /user/jl11257/big_data_project/visualizations/road_time_series
hdfs dfs -setfacl -R -m user:yl3750:r-x /user/jl11257/big_data_project/visualizations/total_trip_time
hdfs dfs -setfacl -R -m user:hls327:r-x /user/jl11257/big_data_project/visualizations/histogram
hdfs dfs -setfacl -R -m user:hls327:r-x /user/jl11257/big_data_project/visualizations/edge_time_series
hdfs dfs -setfacl -R -m user:hls327:r-x /user/jl11257/big_data_project/visualizations/road_time_series
hdfs dfs -setfacl -R -m user:hls327:r-x /user/jl11257/big_data_project/visualizations/total_trip_time
hdfs dfs -setfacl -R -m user:jl11257:r-x /user/jl11257/big_data_project/visualizations/histogram
hdfs dfs -setfacl -R -m user:jl11257:r-x /user/jl11257/big_data_project/visualizations/edge_time_series
hdfs dfs -setfacl -R -m user:jl11257:r-x /user/jl11257/big_data_project/visualizations/road_time_series
hdfs dfs -setfacl -R -m user:jl11257:r-x /user/jl11257/big_data_project/visualizations/total_trip_time

hdfs dfs -get /user/jl11257/big_data_project/visualizations/histogram
hdfs dfs -get /user/jl11257/big_data_project/visualizations/edge_time_series
hdfs dfs -get /user/jl11257/big_data_project/visualizations/road_time_series
hdfs dfs -get /user/jl11257/big_data_project/visualizations/total_trip_time

mkdir visualizations
mv histogram visualizations
mv edge_time_series visualizations
mv road_time_series visualizations
mv total_trip_time visualizations
zip -r visualizations.zip visualizations
rm -r visualizations