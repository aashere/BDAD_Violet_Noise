/scratch/hls327/sumovenv/bin/python /home/hls327/BDAD_Violet_Noise/sumoDataGeneration/parseData.py

hdfs dfs -mkdir /user/jl11257/big_data_project/traces
hdfs dfs -mkdir /user/jl11257/big_data_project/traces/raw
hdfs dfs -mkdir /user/jl11257/big_data_project/traces/processed
hdfs dfs -mkdir /user/jl11257/big_data_project/graph


hdfs dfs -put /scratch/hls327/graphStructure/* /user/jl11257/big_data_project/graph
hdfs dfs -put /scratch/hls327/traces/* /user/jl11257/big_data_project/traces/raw

hdfs dfs -setfacl -R -m user:hls327:rwx /user/jl11257/big_data_project/traces
hdfs dfs -setfacl -R -m user:hls327:rwx /user/jl11257/big_data_project/graph

hdfs dfs -setfacl -R -m user:as12366:r-x /user/jl11257/big_data_project/traces
hdfs dfs -setfacl -R -m user:as12366:r-x /user/jl11257/big_data_project/graph

hdfs dfs -setfacl -R -m user:yl3750:r-x /user/jl11257/big_data_project/traces
hdfs dfs -setfacl -R -m user:yl3750:r-x /user/jl11257/big_data_project/graph

hdfs dfs -setfacl -R -m group::r-x /user/jl11257/big_data_project/traces
hdfs dfs -setfacl -R -m group::r-x /user/jl11257/big_data_project/graph

hdfs dfs -mkdir /user/jl11257/big_data_project/features
hdfs dfs -mkdir /user/jl11257/big_data_project/visualizations


hdfs dfs -setfacl -R -m user:hls327:rwx /user/jl11257/big_data_project/features
hdfs dfs -setfacl -R -m user:hls327:rwx /user/jl11257/big_data_project/visualizations

hdfs dfs -setfacl -R -m user:as12366:rwx /user/jl11257/big_data_project/features
hdfs dfs -setfacl -R -m user:as12366:rwx /user/jl11257/big_data_project/visualizations

hdfs dfs -setfacl -R -m user:yl3750:rwx /user/jl11257/big_data_project/features
hdfs dfs -setfacl -R -m user:yl3750:rwx /user/jl11257/big_data_project/visualizations

hdfs dfs -setfacl -R -m group::rwx /user/jl11257/big_data_project/features
hdfs dfs -setfacl -R -m group::rwx /user/jl11257/big_data_project/visualizations

hdfs dfs -mkdir /user/jl11257/big_data_project/testing
hdfs dfs -setfacl -R -m user:hls327:rwx /user/jl11257/big_data_project/testing
hdfs dfs -setfacl -R -m user:as12366:rwx /user/jl11257/big_data_project/testing
hdfs dfs -setfacl -R -m user:yl3750:rwx /user/jl11257/big_data_project/testing
hdfs dfs -setfacl -R -m group::rwx /user/jl11257/big_data_project/testing

hdfs dfs -mkdir /user/jl11257/big_data_project/predictions
hdfs dfs -setfacl -R -m user:hls327:rwx /user/jl11257/big_data_project/predictions
hdfs dfs -setfacl -R -m user:as12366:rwx /user/jl11257/big_data_project/predictions
hdfs dfs -setfacl -R -m user:yl3750:rwx /user/jl11257/big_data_project/predictions
hdfs dfs -setfacl -R -m group::rwx /user/jl11257/big_data_project/predictions