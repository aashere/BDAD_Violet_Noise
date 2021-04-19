python generateRoutes.py 1 15.0

/scratch/work/public/singularity/run-sumo-1.9.0.bash netconvert --node-files data/net/my_nodes.nod.xml --edge-files data/net/my_edge.edg.xml -t data/net/my_type.type.xml -o data/net/my_net.net.xml
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo -c data/configs/week_0_day_0_config.sumocfg  --save-state.times 86399 --save-state.files data/states/t86399_state.xml --summary-output /scratch/work/public/violetnoise/week_0_day_0_summary.xml --fcd-output /scratch/work/public/violetnoise/week_0_day_0_Trace.xml