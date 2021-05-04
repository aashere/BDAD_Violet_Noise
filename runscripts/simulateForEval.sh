#/scratch/hls327/sumovenv/bin/python /scratch/hls327/sumoDataGeneration/generateRoutes.py 25.0

/scratch/work/public/singularity/run-sumo-1.9.0.bash netconvert \
	--node-files /scratch/hls327/sumoDataGeneration/data/net/my_nodes.nod.xml \
	--edge-files /scratch/hls327/sumoDataGeneration/data/net/my_edge.edg.xml \
	-t /scratch/hls327/sumoDataGeneration/data/net/my_type.type.xml \
	-o /scratch/hls327/sumoDataGeneration/data/net/my_net.net.xml

/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \
	-c /scratch/hls327/sumoDataGeneration/data/configs/week_10_day_0_config.sumocfg \
	--load-state /scratch/hls327/states/last_state.xml \
	--save-state.times 6134399 \
	--save-state.files /scratch/hls327/states/last_state.xml \
	--fcd-output /scratch/hls327/traces/week_10_day_0_trace.xml \
	--no-warnings true