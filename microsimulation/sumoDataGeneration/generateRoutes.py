from generateSumoPath import PathGenerator
import argparse

config_schema = '''<configuration> 

<input> 
<net-file value="/home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/net/my_net.net.xml"/> 
<route-files value="%s"/> 
<additional-files value="/home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/net/bus_stops.xml"/>
</input> 
<time> 
<begin value="%s"/> 
<end value="%s"/> 
</time> 

</configuration> 
'''


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("weeks", type=int)
    parser.add_argument("basevol", type=float)
    args = parser.parse_args()

    generator = PathGenerator(basevol=args.basevol)
    weeks = args.weeks

    for w in range(0,weeks):
        for d in range(0,7):
            if d == 5:
                break
            
            startseconds = (w * 604800) + (d * 86400)
            endseconds = (w * 604800) + ((d+1) * 86400)
            
            routefile = "week_%s_day_%s_route.rou.xml" % (w, d)
            filename = "/home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/routes/" + routefile
            generator.generate_traffic_route(filepath=filename, start_tm_seconds=startseconds, weekday=d)

            configname = "/home/hls327/BDAD_Violet_Noise/microsimulation/sumoDataGeneration/data/configs/week_%s_day_%s_config.sumocfg" % (w,d)
            
            if startseconds > 0:
                startseconds = startseconds - 1

            print(endseconds-1)

            configdata = config_schema % (filename, startseconds, endseconds)
            with open(configname, "w") as f:
                f.write(configdata)
            