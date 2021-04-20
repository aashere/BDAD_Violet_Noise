/home/hls327/sumovenv/bin/python /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/generateRoutes.py 1 50.0
/scratch/work/public/singularity/run-sumo-1.9.0.bash netconvert \
	--node-files /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/net/my_nodes.nod.xml \
	--edge-files /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/net/my_edge.edg.xml \
	-t /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/net/my_type.type.xml \
	-o /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/net/my_net.net.xml
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_0_day_0_config.sumocfg \
	--save-state.times 86399 \
	--save-state.files /scratch/hls327/states/t86399_state.xml \
	--summary-output /scratch/hls327/traces/week_0_day_0_summary.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_0_day_1_config.sumocfg \
	--load-state /scratch/hls327/states/t86399_state.xml \
	--save-state.times 172799 \
	--save-state.files /scratch/hls327/states/t172799_state.xml \
	--summary-output /scratch/hls327/traces/week_0_day_1_summary.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_0_day_2_config.sumocfg \
	--load-state /scratch/hls327/states/t172799_state.xml \
	--save-state.times 259199 \
	--save-state.files /scratch/hls327/states/t259199_state.xml \
	--summary-output /scratch/hls327/traces/week_0_day_2_summary.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_0_day_3_config.sumocfg \
	--load-state /scratch/hls327/states/t259199_state.xml \
	--save-state.times 345599 \
	--save-state.files /scratch/hls327/states/t345599_state.xml \
	--summary-output /scratch/hls327/traces/week_0_day_3_summary.xml \
	--no-warnings true
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_0_day_4_config.sumocfg \
	--load-state /scratch/hls327/states/t345599_state.xml \
	--save-state.times 431999 \
	--save-state.files /scratch/hls327/states/t431999_state.xml \
	--summary-output /scratch/hls327/traces/week_0_day_4_summary.xml \
	--no-warnings true

#/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
#	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_0_day_0_config.sumocfg \
#	--save-state.times 86399 \
#	--save-state.files /scratch/hls327/states/t86399_state.xml \
#	--fcd-output /scratch/hls327/traces/week_0_day_0_trace.xml 
