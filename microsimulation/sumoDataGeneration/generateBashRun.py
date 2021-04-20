from generateSumoPath import PathGenerator
import argparse


bash_schema = '''/scratch/work/public/singularity/run-sumo-1.9.0.bash sumo \\
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
    with open('bash.sh', "w") as f:
        for w in range(0,weeks):
            for d in range(0,7):
                statetime = (w * 604800) + ((d+1) * 86400) - 1
                outp = bash_schema % (w, d, statetime, w, d)
                f.write(outp)
            