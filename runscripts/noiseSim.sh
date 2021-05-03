hdfs dfs -mkdir /user/jl11257/big_data_project/traces/noised

spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 40G --executor-memory 16G \
--num-executors 40 --executor-cores 5 \
--conf spark.yarn.executor.memoryOverhead=2400 \
--class NoiseGenerator /home/$(whoami)/BDAD_Violet_Noise/NoiseGenerator/noisegenerator_2.11-0.1.jar \
/user/jl11257/big_data_project/traces/processed/week_0_* \
/user/jl11257/big_data_project/traces/noised/week_0 12

spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 40G --executor-memory 16G \
--num-executors 40 --executor-cores 5 \
--conf spark.yarn.executor.memoryOverhead=2400 \
--class NoiseGenerator /home/$(whoami)/BDAD_Violet_Noise/NoiseGenerator/noisegenerator_2.11-0.1.jar \
/user/jl11257/big_data_project/traces/processed/week_1_* \
/user/jl11257/big_data_project/traces/noised/week_1 12

spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 40G --executor-memory 16G \
--num-executors 40 --executor-cores 5 \
--conf spark.yarn.executor.memoryOverhead=2400 \
--class NoiseGenerator /home/$(whoami)/BDAD_Violet_Noise/NoiseGenerator/noisegenerator_2.11-0.1.jar \
/user/jl11257/big_data_project/traces/processed/week_2_* \
/user/jl11257/big_data_project/traces/noised/week_2 12

spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 40G --executor-memory 16G \
--num-executors 40 --executor-cores 5 \
--conf spark.yarn.executor.memoryOverhead=2400 \
--class NoiseGenerator /home/$(whoami)/BDAD_Violet_Noise/NoiseGenerator/noisegenerator_2.11-0.1.jar \
/user/jl11257/big_data_project/traces/processed/week_3_* \
/user/jl11257/big_data_project/traces/noised/week_3 12

spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 40G --executor-memory 16G \
--num-executors 40 --executor-cores 5 \
--conf spark.yarn.executor.memoryOverhead=2400 \
--class NoiseGenerator /home/$(whoami)/BDAD_Violet_Noise/NoiseGenerator/noisegenerator_2.11-0.1.jar \
/user/jl11257/big_data_project/traces/processed/week_4_* \
/user/jl11257/big_data_project/traces/noised/week_4 12

spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 40G --executor-memory 16G \
--num-executors 40 --executor-cores 5 \
--conf spark.yarn.executor.memoryOverhead=2400 \
--class NoiseGenerator /home/$(whoami)/BDAD_Violet_Noise/NoiseGenerator/noisegenerator_2.11-0.1.jar \
/user/jl11257/big_data_project/traces/processed/week_5_* \
/user/jl11257/big_data_project/traces/noised/week_5 12

spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 40G --executor-memory 16G \
--num-executors 40 --executor-cores 5 \
--conf spark.yarn.executor.memoryOverhead=2400 \
--class NoiseGenerator /home/$(whoami)/BDAD_Violet_Noise/NoiseGenerator/noisegenerator_2.11-0.1.jar \
/user/jl11257/big_data_project/traces/processed/week_6_* \
/user/jl11257/big_data_project/traces/noised/week_6 12

spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 40G --executor-memory 16G \
--num-executors 40 --executor-cores 5 \
--conf spark.yarn.executor.memoryOverhead=2400 \
--class NoiseGenerator /home/$(whoami)/BDAD_Violet_Noise/NoiseGenerator/noisegenerator_2.11-0.1.jar \
/user/jl11257/big_data_project/traces/processed/week_7_* \
/user/jl11257/big_data_project/traces/noised/week_7 12

spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 40G --executor-memory 16G \
--num-executors 40 --executor-cores 5 \
--conf spark.yarn.executor.memoryOverhead=2400 \
--class NoiseGenerator /home/$(whoami)/BDAD_Violet_Noise/NoiseGenerator/noisegenerator_2.11-0.1.jar \
/user/jl11257/big_data_project/traces/processed/week_8_* \
/user/jl11257/big_data_project/traces/noised/week_8 12

spark-submit --master yarn \
--deploy-mode cluster \
--driver-memory 40G --executor-memory 16G \
--num-executors 40 --executor-cores 5 \
--conf spark.yarn.executor.memoryOverhead=2400 \
--class NoiseGenerator /home/$(whoami)/BDAD_Violet_Noise/NoiseGenerator/noisegenerator_2.11-0.1.jar \
/user/jl11257/big_data_project/traces/processed/week_9_* \
/user/jl11257/big_data_project/traces/noised/week_9 12

hdfs dfs -setfacl -R -m user:hls327:rwx /user/jl11257/big_data_project/traces
hdfs dfs -setfacl -R -m user:as12366:r-x /user/jl11257/big_data_project/traces
hdfs dfs -setfacl -R -m user:yl3750:r-x /user/jl11257/big_data_project/traces
hdfs dfs -setfacl -R -m group::r-x /user/jl11257/big_data_project/traces