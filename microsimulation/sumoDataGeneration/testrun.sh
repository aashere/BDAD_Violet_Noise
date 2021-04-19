python generateRoutes.py

/scratch/work/public/singularity/run-sumo-1.9.0.bash netconvert --node-files data/net/my_nodes.nod.xml --edge-files data/net/my_edge.edg.xml -t data/net/my_type.type.xml -o data/net/my_net.net.xml
/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo -c data/configs/week_0_day_0_config.sumocfg --fcd-output data/traces/week_0_day_0_Trace.xml --summary-output data/traces/week_0_day_0_summary.xml --save-state.times 86399 --save-state.files data/states/t86399_state.xml