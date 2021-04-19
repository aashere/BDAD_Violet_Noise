/home/hls327/sumovenv/bin/python /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/generateRoutes.py 1 15.0
/scratch/work/public/singularity/run-sumo-1.9.0.bash netconvert \
	--node-files /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/net/my_nodes.nod.xml \
	--edge-files /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/net/my_edge.edg.xml \
	-t /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/net/my_type.type.xml \
	-o /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/net/my_net.net.xml
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_0_day_0_config.sumocfg \
	--save-state.times 86399 \
	--save-state.files /scratch/hls327/states/t86399_state.xml \
	--summary-output /scratch/hls327/traces/week_0_day_0_summary.xml
#/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
#	-c ~/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_0_day_0_config.sumocfg \
#	--save-state.times 86399 \
#	--save-state.files ~/scratch/hls327/states/t86399_state.xml \
#	--fcd-output /scratch/hls327/traces/week_0_day_0_Trace.xml 
