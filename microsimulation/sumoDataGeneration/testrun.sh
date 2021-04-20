/home/hls327/sumovenv/bin/python /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/generateRoutes.py 10 25.0
/scratch/work/public/singularity/run-sumo-1.9.0.bash netconvert \
	--node-files /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/net/my_nodes.nod.xml \
	--edge-files /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/net/my_edge.edg.xml \
	-t /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/net/my_type.type.xml \
	-o /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/net/my_net.net.xml
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_0_day_0_config.sumocfg \
	--save-state.times 86399 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_0_day_0_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_0_day_1_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 172799 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_0_day_1_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_0_day_2_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 259199 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_0_day_2_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_0_day_3_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 345599 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_0_day_3_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_0_day_4_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 431999 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_0_day_4_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_0_day_5_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 518399 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_0_day_5_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_0_day_6_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 604799 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_0_day_6_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_1_day_0_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 691199 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_1_day_0_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_1_day_1_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 777599 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_1_day_1_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_1_day_2_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 863999 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_1_day_2_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_1_day_3_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 950399 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_1_day_3_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_1_day_4_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 1036799 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_1_day_4_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_1_day_5_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 1123199 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_1_day_5_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_1_day_6_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 1209599 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_1_day_6_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_2_day_0_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 1295999 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_2_day_0_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_2_day_1_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 1382399 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_2_day_1_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_2_day_2_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 1468799 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_2_day_2_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_2_day_3_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 1555199 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_2_day_3_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_2_day_4_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 1641599 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_2_day_4_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_2_day_5_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 1727999 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_2_day_5_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_2_day_6_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 1814399 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_2_day_6_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_3_day_0_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 1900799 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_3_day_0_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_3_day_1_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 1987199 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_3_day_1_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_3_day_2_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 2073599 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_3_day_2_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_3_day_3_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 2159999 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_3_day_3_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_3_day_4_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 2246399 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_3_day_4_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_3_day_5_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 2332799 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_3_day_5_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_3_day_6_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 2419199 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_3_day_6_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_4_day_0_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 2505599 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_4_day_0_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_4_day_1_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 2591999 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_4_day_1_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_4_day_2_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 2678399 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_4_day_2_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_4_day_3_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 2764799 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_4_day_3_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_4_day_4_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 2851199 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_4_day_4_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_4_day_5_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 2937599 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_4_day_5_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_4_day_6_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 3023999 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_4_day_6_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_5_day_0_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 3110399 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_5_day_0_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_5_day_1_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 3196799 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_5_day_1_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_5_day_2_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 3283199 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_5_day_2_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_5_day_3_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 3369599 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_5_day_3_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_5_day_4_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 3455999 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_5_day_4_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_5_day_5_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 3542399 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_5_day_5_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_5_day_6_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 3628799 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_5_day_6_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_6_day_0_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 3715199 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_6_day_0_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_6_day_1_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 3801599 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_6_day_1_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_6_day_2_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 3887999 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_6_day_2_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_6_day_3_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 3974399 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_6_day_3_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_6_day_4_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 4060799 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_6_day_4_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_6_day_5_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 4147199 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_6_day_5_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_6_day_6_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 4233599 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_6_day_6_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_7_day_0_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 4319999 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_7_day_0_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_7_day_1_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 4406399 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_7_day_1_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_7_day_2_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 4492799 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_7_day_2_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_7_day_3_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 4579199 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_7_day_3_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_7_day_4_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 4665599 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_7_day_4_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_7_day_5_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 4751999 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_7_day_5_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_7_day_6_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 4838399 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_7_day_6_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_8_day_0_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 4924799 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_8_day_0_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_8_day_1_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 5011199 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_8_day_1_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_8_day_2_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 5097599 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_8_day_2_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_8_day_3_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 5183999 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_8_day_3_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_8_day_4_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 5270399 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_8_day_4_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_8_day_5_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 5356799 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_8_day_5_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_8_day_6_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 5443199 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_8_day_6_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_9_day_0_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 5529599 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_9_day_0_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_9_day_1_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 5615999 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_9_day_1_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_9_day_2_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 5702399 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_9_day_2_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_9_day_3_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 5788799 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_9_day_3_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_9_day_4_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 5875199 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_9_day_4_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_9_day_5_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 5961599 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_9_day_5_trace.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_9_day_6_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 6047999 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_9_day_6_trace.xml \
	--no-warnings true