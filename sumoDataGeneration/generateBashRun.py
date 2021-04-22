from generateSumoPath import PathGenerator
import argparse


start_sim = '''/scratch/hls327/sumovenv/bin/python /scratch/hls327/sumoDataGeneration/generateRoutes.py 10 25.0
/scratch/work/public/singularity/run-sumo-1.9.0.bash netconvert \\
	--node-files /scratch/hls327/sumoDataGeneration/data/net/my_nodes.nod.xml \\
	--edge-files /scratch/hls327/sumoDataGeneration/data/net/my_edge.edg.xml \\
	-t /scratch/hls327/sumoDataGeneration/data/net/my_type.type.xml \\
	-o /scratch/hls327/sumoDataGeneration/data/net/my_net.net.xml\n'''

schema_0 = '''/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \\
	-c /scratch/hls327/sumoDataGeneration/data/configs/week_%s_day_%s_config.sumocfg \\
	--save-state.times %s \\
	--save-state.files /scratch/hls327/states/last_state.xml \\
	--fcd-output /scratch/hls327/traces/week_%s_day_%s_trace.xml \\
	--no-warnings true\n'''

schema_loadstate = '''/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \\
	-c /scratch/hls327/sumoDataGeneration/data/configs/week_%s_day_%s_config.sumocfg \\
	--load-state /scratch/hls327/states/last_state.xml \\
	--save-state.times %s \\
	--save-state.files /scratch/hls327/states/last_state.xml \\
	--fcd-output /scratch/hls327/traces/week_%s_day_%s_trace.xml \\
	--no-warnings true\n'''


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("weeks", type=int)
    args = parser.parse_args()

    weeks = args.weeks
    with open('testrun.sh', "w") as f:
        f.write(start_sim)
        for w in range(0,weeks):
            for d in range(0,7):
                statetime = (w * 604800) + ((d+1) * 86400) - 1
                if ((w == 0) and (d == 0)):
                    outp = schema_0 % (w, d, statetime, w, d)
                else:
                    outp = schema_loadstate % (w, d, statetime, w, d)
                f.write(outp)
            